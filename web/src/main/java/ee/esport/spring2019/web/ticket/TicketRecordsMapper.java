package ee.esport.spring2019.web.ticket;

import ee.esport.spring2018.jooq.tables.records.TicketMembersRecord;
import ee.esport.spring2018.jooq.tables.records.TicketOfferingsRecord;
import ee.esport.spring2018.jooq.tables.records.TicketTypesRecord;
import ee.esport.spring2018.jooq.tables.records.TicketsRecord;
import ee.esport.spring2019.web.core.BaseMapper;
import ee.esport.spring2019.web.ticket.domain.Ticket;
import ee.esport.spring2019.web.ticket.domain.TicketType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface TicketRecordsMapper extends BaseMapper {

    @Mapping(target = "id", source = "ticketsRecord.id")
    @Mapping(target = "typeId", source = "offeringsRecord.id")
    @Mapping(target = "offeringId", source = "offeringsRecord.ticketTypeId")
    @Mapping(target = "seat", source = "ticketsRecord.seat")
    @Mapping(target = "status", source = "ticketsRecord.status")
    @Mapping(target = "dateCreated", source = "ticketsRecord.dateCreated")
    @Mapping(target = "members", source = "members")
    Ticket toTicket(TicketsRecord ticketsRecord, TicketOfferingsRecord offeringsRecord, List<Ticket.Member> members);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "igName", source = "igName")
    @Mapping(target = "email", source = "email")
    Ticket.Member toMember(TicketMembersRecord record);

    @Mapping(target = "id", source = "typesRecord.id")
    @Mapping(target = "code", source = "typesRecord.code")
    @Mapping(target = "amountAvailable", source = "typesRecord.amountAvailable")
    @Mapping(target = "amountActive", source = "amountActive")
    @Mapping(target = "teamSize", source = "typesRecord.amountAvailable")
    @Mapping(target = "assignedSeating", source = "typesRecord.assignedSeating")
    @Mapping(target = "offerings", expression = "offerings")
    TicketType toTicketType(TicketTypesRecord typesRecord, List<TicketType.Offering> offerings, Integer amountActive);

    @Mapping(target = "id", source = "offeringsRecord.id")
    @Mapping(target = "name", source = "offeringsRecord.name")
    @Mapping(target = "amountAvailable", source = "offeringsRecord.amountAvailable")
    @Mapping(target = "amountActive", source = "amountActive")
    @Mapping(target = "availableFrom", source = "offeringsRecord.availableForm")
    @Mapping(target = "availableUntil", source = "offeringsRecord.availableUntil")
    @Mapping(target = "availableOnline", source = "offeringsRecord.availableOnline")
    @Mapping(target = "promotional", source = "offeringsRecord.promotional")
    @Mapping(target = "cost", source = "offeringsRecord.cost")
    TicketType.Offering toTicketOffering(TicketOfferingsRecord offeringsRecord, Integer amountActive);

}
