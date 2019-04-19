package ee.esport.spring2019.web.ticket;

import ee.esport.spring2019.jooq.tables.records.*;
import ee.esport.spring2019.web.core.BaseMapper;
import ee.esport.spring2019.web.ticket.domain.Ticket;
import ee.esport.spring2019.web.ticket.domain.TicketCert;
import ee.esport.spring2019.web.ticket.domain.TicketOffering;
import ee.esport.spring2019.web.ticket.domain.TicketType;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface TicketRecordsMapper extends BaseMapper {

    @Mapping(target = "id", source = "ticketsRecord.id")
    @Mapping(target = "name", source = "ticketsRecord.name")
    @Mapping(target = "typeId", source = "typeId")
    @Mapping(target = "offeringId", source = "ticketsRecord.offeringId")
    @Mapping(target = "ownerId", source = "ticketsRecord.ownerId")
    @Mapping(target = "seat", source = "ticketsRecord.seat")
    @Mapping(target = "status", source = "ticketsRecord.status")
    @Mapping(target = "dateCreated", source = "ticketsRecord.dateCreated")
    @Mapping(target = "members", source = "members")
    Ticket toTicket(TicketsRecord ticketsRecord, Integer typeId, List<Ticket.Member> members);

    @Mapping(target = "id", source = "record.id")
    @Mapping(target = "email", source = "record.email")
    Ticket.Member toMember(TicketMembersRecord record);

    @Mapping(target = "id", source = "typesRecord.id")
    @Mapping(target = "code", source = "typesRecord.code")
    @Mapping(target = "amountAvailable", source = "typesRecord.amountAvailable")
    @Mapping(target = "amountRemaining", source = "amountRemaining")
    @Mapping(target = "teamSize", source = "typesRecord.teamSize")
    @Mapping(target = "assignedSeating", source = "typesRecord.assignedSeating")
    TicketType toTicketType(TicketTypesRecord typesRecord, Integer amountRemaining);

    @Mapping(target = "id", source = "offeringsRecord.id")
    @Mapping(target = "name", source = "offeringsRecord.name")
    @Mapping(target = "typeId", source = "offeringsRecord.ticketTypeId")
    @Mapping(target = "amountAvailable", source = "offeringsRecord.amountAvailable")
    @Mapping(target = "amountRemaining", source = "amountRemaining")
    @Mapping(target = "availableFrom", source = "offeringsRecord.availableFrom")
    @Mapping(target = "availableUntil", source = "offeringsRecord.availableUntil")
    @Mapping(target = "availableOnline", source = "offeringsRecord.availableOnline")
    @Mapping(target = "promotional", source = "offeringsRecord.promotional")
    @Mapping(target = "cost", source = "offeringsRecord.cost")
    TicketOffering toTicketOffering(TicketOfferingsRecord offeringsRecord, Integer amountRemaining);

    @Mapping(target = "id", source="id")
    @Mapping(target = "code", source="code")
    @Mapping(target = "memberId", source="memberId")
    @Mapping(target = "timesUsed", source="timesUsed")
    TicketCert toCert(TicketCertsRecord record);

}
