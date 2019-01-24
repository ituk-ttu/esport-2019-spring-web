package ee.esport.spring2019.web.ticket;

import ee.esport.spring2019.jooq.tables.records.TicketMembersRecord;
import ee.esport.spring2019.jooq.tables.records.TicketOfferingsRecord;
import ee.esport.spring2019.jooq.tables.records.TicketTypesRecord;
import ee.esport.spring2019.jooq.tables.records.TicketsRecord;
import ee.esport.spring2019.web.ticket.domain.Ticket;
import ee.esport.spring2019.web.ticket.domain.TicketCandidate;
import ee.esport.spring2019.web.ticket.domain.TicketType;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ee.esport.spring2019.jooq.Tables.*;
import static ee.esport.spring2019.jooq.tables.Tickets.TICKETS;

@Service
@RequiredArgsConstructor
public class TicketRepository {

    private final DSLContext dsl;
    private final TicketRecordsMapper mapper;
    public static final String ACTIVE_TICKETS_COUNT = "ACTIVE_TICKETS_COUNT";

    public Ticket createTicket(TicketCandidate candidate) {
        TicketsRecord ticketsRecord = dsl.insertInto(TICKETS)
                                         .set(TICKETS.DATE_CREATED, getCurrentTimestamp())
                                         .set(TICKETS.OFFERING_ID, candidate.getOfferingId())
                                         .set(TICKETS.OWNER_ID, candidate.getOwnerId())
                                         .set(TICKETS.SEAT, candidate.getSeat())
                                         .set(TICKETS.STATUS, candidate.getStatus().name())
                                         .returning()
                                         .fetchOne();
        return mapper.toTicket(ticketsRecord,
                               getOfferingsRecord(ticketsRecord.getOfferingId()),
                               Collections.emptyList());
    }

    private TicketOfferingsRecord getOfferingsRecord(int offeringId) {
        return dsl.selectFrom(TICKET_OFFERINGS)
                  .where(TICKET_OFFERINGS.ID.eq(offeringId))
                  .fetchOptional()
                  .orElseThrow(() -> new NoSuchElementException("Ticket offering not found"));
    }

    public Ticket getTicket(int id) {
        return getTickets(TICKETS.ID.eq(id)).findAny()
                                            .orElseThrow(() -> new NoSuchElementException("Ticket not found"));
    }

    public List<Ticket> getAllTickets() {
        return getTickets(DSL.trueCondition()).collect(Collectors.toList());
    }

    private Stream<Ticket> getTickets(Condition condition) {
        Stream<Record> ticketAndOfferingRecords = dsl.select()
                                                     .from(TICKETS.leftJoin(TICKET_OFFERINGS).onKey())
                                                     .where(condition)
                                                     .stream();
        Map<Integer, List<TicketMembersRecord>> memberRecordsByTicketId =
                dsl.select(TICKET_MEMBERS.fields())
                   .select(TICKETS.ID)
                   .from(TICKET_MEMBERS.leftJoin(TICKET_OFFERINGS).onKey())
                   .where(condition)
                   .fetchGroups(TICKETS.ID, it -> it.into(TICKET_MEMBERS));
        return ticketAndOfferingRecords.map(it -> {
            TicketsRecord ticketsRecord = it.into(TICKETS);
            TicketOfferingsRecord offeringsRecord = it.into(TICKET_OFFERINGS);
            List<Ticket.Member> members = memberRecordsByTicketId.getOrDefault(ticketsRecord.getId(),
                                                                               Collections.emptyList())
                                                                 .stream()
                                                                 .map(mapper::toMember)
                                                                 .collect(Collectors.toList());
            return mapper.toTicket(ticketsRecord, offeringsRecord, members);
        });
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

    public TicketType getType(int typeId) {
        Stream<TicketType> typeStream = getTicketTypes(DSL.trueCondition());
        return typeStream.findAny()
                         .orElseThrow(() -> new NoSuchElementException("Ticket type not found"));
    }

    private Stream<TicketType> getTicketTypes(Condition condition) {
        Stream<Record> typesRecords = dsl.select(TICKET_TYPES.fields())
                                        .select(TICKETS.ID.count().as(ACTIVE_TICKETS_COUNT))
                                        .from(TICKET_TYPES.leftJoin(TICKET_OFFERINGS)
                                                          .on(TICKET_OFFERINGS.TICKETTYPE_ID.eq(TICKET_TYPES.ID))
                                                          .leftJoin(TICKETS)
                                                          .on(TICKETS.OFFERING_ID.eq(TICKET_OFFERINGS.ID)))
                                        .where(condition)
                                        .groupBy(TICKET_TYPES.ID)
                                        .stream();
        Map<Integer, List<Record>> offeringsByTypeId =
                dsl.select(TICKET_OFFERINGS.fields())
                   .select(TICKET_TYPES.ID)
                   .select(TICKETS.ID.count().as(ACTIVE_TICKETS_COUNT))
                   .from(TICKET_OFFERINGS.leftJoin(TICKET_TYPES)
                                         .onKey()
                                         .leftJoin(TICKETS)
                                         .on(TICKETS.OFFERING_ID.eq(TICKET_OFFERINGS.ID)))
                   .where(condition)
                   .groupBy(TICKET_OFFERINGS.ID)
                   .fetchGroups(TICKET_TYPES.ID, record -> record);
        return typesRecords.map(it -> {
            TicketTypesRecord typesRecord = it.into(TICKET_TYPES);
            List<TicketType.Offering> offerings =
                    offeringsByTypeId.getOrDefault(typesRecord.getId(),
                                                   Collections.emptyList())
                                     .stream()
                                     .map(offering -> {
                                         TicketOfferingsRecord offeringsRecord = offering.into(TICKET_OFFERINGS);
                                         Integer amountActive = offering.get(ACTIVE_TICKETS_COUNT, Integer.class);
                                         return mapper.toTicketOffering(offeringsRecord, amountActive);
                                     })
                    .collect(Collectors.toList());
            Integer amountActive = offerings.stream()
                                            .mapToInt(TicketType.Offering::getAmountActive)
                                            .sum();
            return mapper.toTicketType(typesRecord, offerings, amountActive);
        });
    }

    private Timestamp getCurrentTimestamp() {
        return new Timestamp(Instant.now().getEpochSecond());
    }

}
