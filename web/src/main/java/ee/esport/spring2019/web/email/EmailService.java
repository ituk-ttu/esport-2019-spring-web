package ee.esport.spring2019.web.email;

import ee.esport.spring2019.web.auth.user.UserService;
import ee.esport.spring2019.web.ticket.TicketService;
import ee.esport.spring2019.web.ticket.domain.Ticket;
import ee.esport.spring2019.web.ticket.domain.TicketOffering;
import ee.esport.spring2019.web.ticket.domain.TicketType;
import lombok.RequiredArgsConstructor;
import net.sargue.mailgun.Mail;
import net.sargue.mailgun.MailRequestCallback;
import net.sargue.mailgun.Response;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class EmailService {

    public static final DateTimeFormatter DATE_PATTERN = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    @Resource
    private final MailgunConfiguration mailgunConfig;

    @Resource
    private final VelocityEngine velocityEngine;

    @Resource
    private final UserService userService;

    public CompletableFuture<Response> sendEmail(String type, Ticket ticket, TicketType ticketType, TicketOffering ticketOffering) {
        VelocityContext context = createContext();
        BigDecimal personCost = ticketOffering.getCost().divide(BigDecimal.valueOf(ticketType.getTeamSize()));
        context.put("ticket", ticket);
        context.put("ticketType", ticketType);
        context.put("ticketOffering", ticketOffering);
        context.put("perPerson", personCost.toString());
        context.put("invoiceNumber",
                "2019-359027-" + "000".substring(Integer.toString(ticket.getId()).length()) + ticket.getId());
        context.put("payByDate",
                OffsetDateTime.ofInstant(ticket.getDateCreated().plusDays(3).toInstant(), ZoneId.systemDefault()));
        context.put("date", OffsetDateTime.now());
        String to = getOwnerId(ticket.getOwnerId());
        switch (type) {
            case "ticketReserved":
                return sendAsync(to, "ticketReserved", context, "Pilet reserveeritud / Ticket Reserved");
            case "ticketWaiting":
                return sendAsync(getOwnerId(ticket.getOwnerId()), "ticketWaiting", context, "Pilet ootel / Ticket In Waiting List ");
            case "ticketCanceled":
                return sendAsync(getOwnerId(ticket.getOwnerId()), "ticketCanceled", context, "Pilet tühistatud / Ticket Canceled");
            case "ticketConfirmed":
                return sendAsync(getOwnerId(ticket.getOwnerId()), "ticketConfirmed", context, "Pilet kinnitatud / Ticket Confirmed");
        }
        throw new RuntimeException();
    }

    private String getOwnerId(Integer ownerId) {
        return userService.getUserEmail(ownerId);
    }

    private CompletableFuture<Response> sendAsync(String to, String templateName, VelocityContext context,
                                                  String subject) {
        Mail mail = Mail.using(mailgunConfig)
                        .to(to)
                        .subject(subject)
                        .html(renderTemplate("html/" + templateName, context))
                        .text(renderTemplate("plain/" + templateName, context))
                        .build();
        return sendAsync(mail);
    }

    private CompletableFuture<Response> sendAsync(Mail mail) {
        CompletableFuture<Response> future = new CompletableFuture<>();
        mail.sendAsync(new MailRequestCallback() {
            @Override
            public void completed(Response response) {
                future.complete(response);
            }

            @Override
            public void failed(Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });
        return future;
    }

    private String renderTemplate(String templateName, VelocityContext context) {
        StringWriter writer = new StringWriter();
        try {
            velocityEngine.getTemplate("emailTemplates/" + templateName + ".vm").merge(context, writer);
            return writer.getBuffer().toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private VelocityContext createContext() {
        VelocityContext context = new VelocityContext();
        context.put("datePattern", DATE_PATTERN);
        return context;
    }
}
