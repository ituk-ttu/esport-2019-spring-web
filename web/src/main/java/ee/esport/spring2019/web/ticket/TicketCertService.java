package ee.esport.spring2019.web.ticket;

import ee.esport.spring2019.web.email.EmailService;
import ee.esport.spring2019.web.ticket.domain.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketCertService {

    private static final String CODE_CHARS = "23456789ABCDEFGHJKLMNPQRSZTUVWXY";
    private static final int CODE_LENGTH = 8;
    private static final Random RANDOM = new SecureRandom();

    @Resource
    private final TicketCertRepository repository;

    @Resource
    private final EmailService emailService;

    @Resource
    private final TicketService ticketService;

    @Transactional
    public List<TicketCert> generateCerts(int ticketId) {
        Ticket ticket = ticketService.getTicket(ticketId);
        if (ticket.getStatus() != Ticket.Status.PAID) {
            throw new IllegalArgumentException("Ticket not in PAID state");
        }
        if (repository.getCerts(ticket.getId()).size() > 0) {
            throw new IllegalStateException("Ticket already has certs");
        }
        TicketType type = ticketService.getType(ticket.getTypeId());
        TicketOffering offering = ticketService.getFromAllOfferings(ticket.getOfferingId());
        List<MemberCertEntry> memberCerts = ticket.getMembers()
                                             .stream()
                                             .map(it -> createMemberCert(ticket, it))
                                             .collect(Collectors.toList());
        TicketCert ownerCert = null;
        if (type.getTeamSize() > 1) {
            ownerCert = repository.createCert(ticketId, new TicketCertCandidate(generateCode(), null));
            emailService.sendCertOwner(ticket, offering, ownerCert);
        }
        memberCerts.forEach(it -> emailService.sendCert(ticket, offering, it.getMember(), it.getCert()));
        return Stream.concat(ownerCert != null ? Stream.of(ownerCert) : Stream.empty(),
                             memberCerts.stream().map(MemberCertEntry::getCert))
                     .collect(Collectors.toList());
    }

    private MemberCertEntry createMemberCert(Ticket ticket, Ticket.Member member) {
        TicketCertCandidate ticketCertCandidate = new TicketCertCandidate(generateCode(), member.getId());
        TicketCert cert = repository.createCert(ticket.getId(), ticketCertCandidate);
        return new MemberCertEntry(member, cert);
    }

    private String generateCode() {
        return IntStream.range(0, CODE_LENGTH)
                        .map(it -> RANDOM.nextInt())
                        .mapToObj(this::getCodeChar)
                        .collect(Collectors.joining());
    }

    private String getCodeChar(int i) {
        int index = Math.floorMod(i, CODE_CHARS.length());
        return CODE_CHARS.substring(index, index + 1);
    }

    @Value
    private static class MemberCertEntry {

        @NonNull private final Ticket.Member member;
        @NonNull private final TicketCert cert;

    }

}
