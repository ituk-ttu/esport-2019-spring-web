package ee.esport.spring2019.web.email;

import ee.esport.spring2019.web.ticket.domain.Ticket;
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

    public CompletableFuture<Response> sendTicketReservation(Ticket ticket, String loginLink) {
        VelocityContext context = createContext();
        context.put("ticket", ticket);
        context.put("loginLink", loginLink);
        context.put("invoiceNumber",
                    "2018-359027-" + "000".substring(Integer.toString(ticket.getId()).length()) + ticket.getId());
        context.put("payByDate",
                    OffsetDateTime.ofInstant(ticket.getDateCreated().plusDays(3).toInstant(), ZoneId.systemDefault()));
        String to = getOwnerId();
        return sendAsync(to, "ticketReserved", context, "Pilet reserveeritud / Ticket Reserved");
    }

    private String getOwnerId() {
        throw new NotImplementedException("Getting owner email nott ye implemented");
    }

    public CompletableFuture<Response> sendTicketWaiting(Ticket ticket, String loginLink) {
        VelocityContext context = createContext();
        context.put("ticket", ticket);
        context.put("loginLink", loginLink);
        context.put("invoiceNumber",
                    "2018-359027-" + "000".substring(Integer.toString(ticket.getId()).length()) + ticket.getId());
        context.put("payByDate",
                    OffsetDateTime.ofInstant(ticket.getDateCreated().plusDays(3).toInstant(), ZoneId.systemDefault()));
        return sendAsync(getOwnerId(), "ticketWaiting", context, "Pilet ootel / Ticket In Waiting List ");
    }

    public CompletableFuture<Response> sendTicketCanceled(Ticket ticket, String loginLink) {
        VelocityContext context = createContext();
        context.put("ticket", ticket);
        context.put("loginLink", loginLink);
        return sendAsync(getOwnerId(), "ticketCanceled", context, "Pilet tühistatud / Ticket Canceled");
    }

    public CompletableFuture<Response> sendTicketConfirmed(Ticket ticket, String loginLink) {
        VelocityContext context = createContext();
        context.put("ticket", ticket);
        context.put("loginLink", loginLink);
        return sendAsync(getOwnerId(), "ticketConfirmed", context, "Pilet kinnitatud / Ticket Confirmed");
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
