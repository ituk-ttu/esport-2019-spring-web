package ee.esport.spring2019.web.ticket;

import ee.esport.spring2019.jooq.Keys;
import ee.esport.spring2019.jooq.tables.records.TicketMembersRecord;
import ee.esport.spring2019.jooq.tables.records.TicketTypesRecord;
import ee.esport.spring2019.jooq.tables.records.TicketsRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.UpdateSetStep;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ee.esport.spring2019.jooq.Tables.*;
import static org.jooq.impl.DSL.count;

@Service
public class TicketRepository {

    @Resource
    private DSLContext dsl;

    private RecordMapper<Record, Ticket> ticketRecordMapper = record -> {
        TicketsRecord ticketsRecord = record.into(TICKETS);
        Ticket ticket = ticketsRecord.into(Ticket.class);
        ticket.setType(record.into(TICKET_TYPES).into(TicketType.class));
        ticket.setMembers(ticketsRecord.fetchChildren(Keys.TICKET_MEMBERS_TICKETS_ID_FK)
                                       .into(TicketMember.class));
        return ticket;
    };

    public int addType(TicketType type) {
        TicketTypesRecord record = dsl.newRecord(TICKET_TYPES, type);
        record.store();
        return record.getId();
    }

    public List<TicketType> getAllTypes() {
        return withRelationsRelations(getAllTicketTypesFlat()).stream()
                                                              .filter(type -> type.getParentTicketTypeId() == null)
                                                              .map(this::fixAmountReserved)
                                                              .collect(Collectors.toList());
    }

    private TicketType fixAmountReserved(TicketType type) {
        if (type.getPromotions() == null) {
            return type;
        }
        type.getPromotions().forEach(this::fixAmountReserved);
        type.setAmountReserved(type.getAmountReserved() + type.getPromotions()
                                                                .stream()
                                                                .mapToInt(TicketType::getAmountReserved)
                                                                .sum());
        return type;
    }


    private List<TicketType> withRelationsRelations(List<TicketType> ticketTypes) {
        Map<Integer, TicketType> typesById = ticketTypes.stream()
                                                        .collect(Collectors.toMap(TicketType::getId,
                                                                                  Function.identity()));
        ticketTypes.stream()
                   .filter(type -> type.getParentTicketTypeId() != null)
                   .forEach(type -> {
                       TicketType parentType = typesById.get(type.getParentTicketTypeId());
                       if (parentType.getPromotions() == null) {
                           parentType.setPromotions(new ArrayList<>());
                       }
                       parentType.getPromotions().add(type);
                   });

        return ticketTypes;
    }

    private List<TicketType> getAllTicketTypesFlat() {
        return dsl.select(Stream.concat(Arrays.stream(TICKET_TYPES.fields()),
                                        Stream.of(count(TICKETS).as("amountReserved")))
                                .collect(Collectors.toList()))
                  .from(TICKET_TYPES)
                  .leftJoin(TICKETS)
                  .on(TICKET_TYPES.ID.eq(TICKETS.TYPE_ID)
                                     .and(TICKETS.STATUS.in(Arrays.asList(TicketStatus.IN_WAITING_LIST,
                                                                          TicketStatus.AWAITING_PAYMENT,
                                                                          TicketStatus.PAID))))
                  .groupBy(TICKET_TYPES.ID)
                  .fetchInto(TicketType.class);
    }

    public int addTicket(Ticket ticket) {
        TicketsRecord record = dsl.newRecord(TICKETS, ticket)
                                  .with(TICKETS.TYPE_ID, Optional.of(ticket)
                                                                 .map(Ticket::getType)
                                                                 .map(TicketType::getId)
                                                                 .orElse(null));
        record.store();
        return record.getId();
    }

    public String createLoginLink(int ticketId) {
        String name = UUID.randomUUID()
                          .toString();
        dsl.newRecord(TICKET_LOGIN_LINKS)
           .with(TICKET_LOGIN_LINKS.NAME, name)
           .with(TICKET_LOGIN_LINKS.TICKET_ID, ticketId)
           .store();
        return name;
    }

    public Integer getLoginLinkTicketId(String name) {
        return dsl.select(TICKET_LOGIN_LINKS.TICKET_ID)
                  .from(TICKET_LOGIN_LINKS)
                  .where(TICKET_LOGIN_LINKS.NAME.eq(name))
                  .fetchAny(TICKET_LOGIN_LINKS.TICKET_ID);
    }

    public List<Ticket> getAllTickets() {
        return dsl.select()
                  .from(TICKETS)
                  .leftJoin(TICKET_TYPES)
                  .onKey()
                  .fetch(ticketRecordMapper);
    }

    public List<Ticket> getWaitingListTickets(int ticketTypeId, int limit) {
        return dsl.select()
                  .from(TICKETS)
                  .leftJoin(TICKET_TYPES)
                  .onKey()
                  .where(TICKETS.TYPE_ID.eq(ticketTypeId))
                  .and(TICKETS.STATUS.eq(TicketStatus.IN_WAITING_LIST.name()))
                  .orderBy(TICKETS.DATE_CREATED.asc())
                  .limit(limit)
                  .fetch(ticketRecordMapper);
    }

    public TicketMember addMember(int ticketId, TicketMember member) {
        TicketMembersRecord record = dsl.newRecord(TICKET_MEMBERS, member);
        record.with(TICKET_MEMBERS.TICKET_ID, ticketId)
              .insert();
        return record.into(TicketMember.class);
    }

    public TicketMember updateMember(TicketMember member) {
        TicketMembersRecord record = dsl.newRecord(TICKET_MEMBERS, member);
        record.reset(TICKET_MEMBERS.TICKET_ID);
        record.update();
        return record.into(TicketMember.class);
    }

    public void deleteMember(int ticketId, int memberId) {
        dsl.delete(TICKET_MEMBERS)
           .where(TICKET_MEMBERS.ID.eq(memberId))
           .and(TICKET_MEMBERS.TICKET_ID.eq(ticketId))
           .execute();
    }

    public int getAmountReserved(int typeId) {
        List<Integer> typeIds = new ArrayList<>();
        Queue<TicketType> types = new ArrayDeque<>(Collections.singletonList(getTicketType(typeId)));
        while (!types.isEmpty()) {
            TicketType type = types.poll();
            typeIds.add(type.getId());
            List<TicketType> promotions = type.getPromotions();
            if (promotions != null) {
                types.addAll(promotions);
            }
        }
        return dsl.selectCount()
                  .from(TICKETS)
                  .where(TICKETS.TYPE_ID.in(typeIds))
                  .and(TICKETS.STATUS.in(Arrays.asList(TicketStatus.AWAITING_PAYMENT,
                                                       TicketStatus.PAID)))
                  .fetchAny()
                  .into(Integer.class);
    }

    //TODO: optimize
    public TicketType getTicketType(int typeId) {
        Queue<TicketType> types = new ArrayDeque<>(getAllTypes());
        while (!types.isEmpty()) {
            TicketType type = types.poll();
            if (type.getId() == typeId) {
                return type;
            }
            List<TicketType> promotions = type.getPromotions();
            if (promotions != null) {
                types.addAll(promotions);
            }
        }
        return null;
    }

    public Ticket getTicket(int ticketId) {
        return dsl.select()
                  .from(TICKETS)
                  .leftJoin(TICKET_TYPES)
                  .onKey()
                  .where(TICKETS.ID.eq(ticketId))
                  .fetchAny(ticketRecordMapper);
    }

    public void setStatus(int id, TicketStatus status) {
        UpdateSetStep<TicketsRecord> update = dsl.update(TICKETS);
        if (status == TicketStatus.AWAITING_PAYMENT) {
            update = update.set(TICKETS.DATE_CREATED, Timestamp.from(Instant.now()));
        }
        update.set(TICKETS.STATUS, status.name())
              .where(TICKETS.ID.eq(id))
              .execute();
    }
}
