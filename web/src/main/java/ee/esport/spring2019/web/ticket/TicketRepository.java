package ee.esport.spring2019.web.ticket;

import ee.esport.spring2019.jooq.tables.records.*;
import ee.esport.spring2019.web.ticket.domain.*;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ee.esport.spring2019.jooq.Tables.*;
import static ee.esport.spring2019.jooq.tables.Tickets.TICKETS;

@Service
@RequiredArgsConstructor
public class TicketRepository {

    private static final String ACTIVE_TICKETS_COUNT = "ACTIVE_TICKETS_COUNT";
    private static final Condition ACTIVE_TICKET_CONDITION = TICKETS.STATUS.notEqual(Ticket.Status.CANCELED.name());

    private final DSLContext dsl;
    private final TicketRecordsMapper mapper;

    public Ticket createTicket(TicketCandidate candidate) {
        TicketsRecord ticketsRecord = dsl.insertInto(TICKETS)
                                         .set(TICKETS.DATE_CREATED, getCurrentTimestamp())
                                         .set(TICKETS.OFFERING_ID, candidate.getOfferingId())
                                         .set(TICKETS.OWNER_ID, candidate.getOwnerId())
                                         .set(TICKETS.SEAT, candidate.getSeat())
                                         .set(TICKETS.STATUS, candidate.getStatus().name())
                                         .set(TICKETS.NAME, candidate.getName())
                                         .returning()
                                         .fetchOne();
        return mapper.toTicket(ticketsRecord,
                               getOffering(ticketsRecord.getOfferingId()).getTypeId(),
                               Collections.emptyList());
    }

    public Ticket getTicket(int id) {
        return getTickets(TICKETS.ID.eq(id)).findAny()
                                            .orElseThrow(() -> new NoSuchElementException("Ticket not found"));
    }

    public List<Ticket> getUserTickets(int userId) {
        return getTickets(TICKETS.OWNER_ID.eq(userId)).collect(Collectors.toList());
    }

    public List<Ticket> getAllTickets() {
        return getTickets(DSL.trueCondition()).collect(Collectors.toList());
    }

    private Stream<Ticket> getTickets(Condition condition) {
        Stream<Record> ticketAndOfferingRecords = dsl.select(TICKETS.fields())
                                                     .select(TICKET_OFFERINGS.TICKETTYPE_ID)
                                                     .from(TICKETS.leftJoin(TICKET_OFFERINGS).onKey())
                                                     .where(condition)
                                                     .stream();
        Map<Integer, List<TicketMembersRecord>> memberRecordsByTicketId =
                dsl.select(TICKET_MEMBERS.fields())
                   .select(TICKETS.ID)
                   .from(TICKET_MEMBERS.leftJoin(TICKETS).onKey())
                   .where(condition)
                   .fetchGroups(TICKETS.ID, it -> it.into(TICKET_MEMBERS));
        Map<Integer, UsersRecord> ticketOwners =
                dsl.select(USERS.fields())
                   .select(TICKETS.ID)
                   .from(TICKETS.leftJoin(USERS).onKey())
                   .where(condition)
                   .fetchMap(TICKETS.ID, it -> it.into(USERS));
        return ticketAndOfferingRecords.map(it -> {
            TicketsRecord ticketsRecord = it.into(TICKETS);
            UsersRecord ownerRecord = ticketOwners.get(ticketsRecord.getId());
            Ticket.Member ownerMember = new Ticket.Member(null, ownerRecord.getEmail());
            List<Ticket.Member> members = Stream.concat(Stream.of(ownerMember),
                                                        memberRecordsByTicketId.getOrDefault(ticketsRecord.getId(),
                                                                                             Collections.emptyList())
                                                                               .stream()
                                                                               .map(mapper::toMember))
                                                .collect(Collectors.toList());
            return mapper.toTicket(ticketsRecord, it.get(TICKET_OFFERINGS.TICKETTYPE_ID), members);
        });
    }

    public Ticket.Member updateMember(int ticketId, int memberId, TicketMemberCandidate candidate) {
        int rowsUpdated = dsl.update(TICKET_MEMBERS)
                             .set(TICKET_MEMBERS.EMAIL, candidate.getEmail())
                             .where(TICKET_MEMBERS.TICKET_ID.eq(ticketId))
                             .and(TICKET_MEMBERS.ID.eq(memberId))
                             .execute();

        if (rowsUpdated == 0) {
            throw new NoSuchElementException("Ticket member not found");
        }
        return mapper.toMember(dsl.selectFrom(TICKET_MEMBERS)
                                  .where(TICKET_MEMBERS.TICKET_ID.eq(ticketId))
                                  .and(TICKET_MEMBERS.ID.eq(memberId))
                                  .fetchAny());
    }

    public Ticket.Member addMember(int ticketId, TicketMemberCandidate member) {
        return mapper.toMember(dsl.insertInto(TICKET_MEMBERS)
                                  .set(TICKET_MEMBERS.TICKET_ID, ticketId)
                                  .set(TICKET_MEMBERS.EMAIL, member.getEmail())
                                  .returning()
                                  .fetchOne());
    }

    public void deleteMember(int ticketId, int memberId) {
        int rowsDeleted = dsl.delete(TICKET_MEMBERS)
                             .where(TICKET_MEMBERS.TICKET_ID.eq(ticketId))
                             .and(TICKET_MEMBERS.ID.eq(memberId))
                             .execute();
        if (rowsDeleted == 0) {
            throw new NoSuchElementException("Ticket member not found");
        }
    }

    public List<TicketType> getAllTypes() {
        return getTicketTypes(DSL.trueCondition()).collect(Collectors.toList());
    }

    public TicketType getType(int id) {
        Stream<TicketType> typeStream = getTicketTypes(TICKET_TYPES.ID.eq(id));
        return typeStream.findAny()
                         .orElseThrow(() -> new NoSuchElementException("Ticket type not found"));
    }

    public TicketOffering getOffering(int id) {
        Stream<TicketOffering> offeringStream = getTicketOfferings(TICKET_OFFERINGS.ID.eq(id));
        return offeringStream.findAny()
                             .orElseThrow(() -> new NoSuchElementException("Ticket offering not found"));
    }

    public List<TicketOffering> getAllOfferings() {
        return getTicketOfferings(DSL.trueCondition()).collect(Collectors.toList());
    }

    private Stream<TicketOffering> getTicketOfferings(Condition condition) {
        Stream<Record> records = dsl.select(TICKET_OFFERINGS.fields())
                                   .select(TICKET_TYPES.ID)
                                   .select(TICKETS.ID.count().as(ACTIVE_TICKETS_COUNT))
                                   .from(TICKET_OFFERINGS.leftJoin(TICKET_TYPES)
                                                         .onKey()
                                                         .leftJoin(TICKETS)
                                                         .on(TICKETS.OFFERING_ID.eq(TICKET_OFFERINGS.ID))
                                                         .and(ACTIVE_TICKET_CONDITION))
                                   .where(condition)
                                   .groupBy(TICKET_OFFERINGS.ID)
                                   .stream();
        return records.map(record -> {
            TicketOfferingsRecord offeringsRecord = record.into(TICKET_OFFERINGS);
            Integer offeringAmountAvailable = offeringsRecord.getAmountAvailable();
            Integer amountActive = record.get(ACTIVE_TICKETS_COUNT, Integer.class);
            Integer offeringAmountRemaining = offeringAmountAvailable != null ?
                                              Math.max(offeringAmountAvailable - amountActive, 0) :
                                              null;
            Integer typeAmountRemaining = getType(offeringsRecord.getTicketTypeId()).getAmountRemaining();
            Integer amountRemaining = Stream.of(offeringAmountRemaining, typeAmountRemaining)
                                            .filter(Objects::nonNull)
                                            .reduce(Integer::min)
                                            .orElse(null);
            return mapper.toTicketOffering(offeringsRecord, amountRemaining);
        });
    }

    private Stream<TicketType> getTicketTypes(Condition condition) {
        Stream<Record> records = dsl.select(TICKET_TYPES.fields())
                                        .select(TICKETS.ID.count().as(ACTIVE_TICKETS_COUNT))
                                        .from(TICKET_TYPES.leftJoin(TICKET_OFFERINGS)
                                                          .on(TICKET_OFFERINGS.TICKETTYPE_ID.eq(TICKET_TYPES.ID))
                                                          .leftJoin(TICKETS)
                                                          .on(TICKETS.OFFERING_ID.eq(TICKET_OFFERINGS.ID))
                                                          .and(ACTIVE_TICKET_CONDITION))
                                        .where(condition)
                                        .groupBy(TICKET_TYPES.ID)
                                        .stream();
        return records.map(record -> {
            TicketTypesRecord typesRecord = record.into(TICKET_TYPES);
            Integer amountActive = record.get(ACTIVE_TICKETS_COUNT, Integer.class);
            Integer amountAvailable = typesRecord.getAmountAvailable();
            Integer amountRemaining = amountAvailable != null ?
                                      Math.max(amountAvailable - amountActive, 0) :
                                      null;
            return mapper.toTicketType(typesRecord, amountRemaining);
        });
    }

    private Timestamp getCurrentTimestamp() {
        return Timestamp.valueOf(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
    }

    public void confirmTicketPaid(Ticket ticket) {
        dsl.update(TICKETS)
                .set(TICKETS.STATUS, Ticket.Status.PAID.toString())
                .where(TICKETS.ID.eq(ticket.getId()))
                .execute();
    }

    public void cancelTicket(Ticket ticket) {
        dsl.update(TICKETS)
                .set(TICKETS.STATUS, Ticket.Status.CANCELED.toString())
                .where(TICKETS.ID.eq(ticket.getId()))
                .execute();
    }

    public void setSeat(int ticketId, Integer seat) {
        dsl.update(TICKETS)
           .set(TICKETS.SEAT, seat)
           .where(TICKETS.ID.eq(ticketId))
           .execute();
    }
}
