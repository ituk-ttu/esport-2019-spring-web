package ee.esport.spring2019.web.ticket;

import ee.esport.spring2019.jooq.tables.records.TicketCertsRecord;
import ee.esport.spring2019.web.ticket.domain.Ticket;
import ee.esport.spring2019.web.ticket.domain.TicketCert;
import ee.esport.spring2019.web.ticket.domain.TicketCertCandidate;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static ee.esport.spring2019.jooq.Tables.TICKET_CERTS;
import static ee.esport.spring2019.jooq.tables.Tickets.TICKETS;

@Service
@RequiredArgsConstructor
public class TicketCertRepository {

    private final DSLContext dsl;
    private final TicketRecordsMapper mapper;

    public TicketCert createCert(int ticketId, TicketCertCandidate candidate) {
        TicketCertsRecord ticketCertsRecord = dsl.insertInto(TICKET_CERTS)
                                                 .set(TICKET_CERTS.CODE, candidate.getCode())
                                                 .set(TICKET_CERTS.TICKET_ID, ticketId)
                                                 .set(TICKET_CERTS.MEMBER_ID, candidate.getMemberId())
                                                 .set(TICKET_CERTS.TIMES_USED, 0)
                                                 .returning()
                                                 .fetchOne();
        return mapper.toCert(ticketCertsRecord);
    }

    public List<TicketCert> getCerts(int ticketId) {
        return dsl.select(TICKET_CERTS.fields())
                  .from(TICKET_CERTS)
                  .where(TICKET_CERTS.TICKET_ID.eq(ticketId))
                  .stream()
                  .map(it -> it.into(TICKET_CERTS))
                  .map(mapper::toCert)
                  .collect(Collectors.toList());
    }

}