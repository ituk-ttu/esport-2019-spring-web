package ee.esport.spring2019.web.ticket;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Data
public class TicketType {

    private int Id;
    private String name;
    private Integer amountAvailable;
    private BigDecimal cost;
    private OffsetDateTime availableFrom;
    private OffsetDateTime availableUntil;
    private Integer parentTicketTypeId;
    private Integer teamSize;
    private Integer atLocationCost;
    private int amountReserved;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<TicketType> promotions;

    public BigDecimal getPerPersonCost() {
        return cost.divide(BigDecimal.valueOf(teamSize), BigDecimal.ROUND_HALF_UP);
    }

    public boolean hasRemaining() {
        return getAmountAvailable() == null || getAmountAvailable() > getAmountReserved();
    }

    public Integer getAmountRemaining() {
        return getAmountAvailable() != null ? getAmountAvailable() - getAmountReserved() : null;
    }

    public void decrementRemaining() {
        amountReserved--;
    }

}
