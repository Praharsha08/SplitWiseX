package logic;

import models.Money;

import java.util.ArrayList;
import java.util.List;

public class SplitUtil {
    public static List<Money> splitEqual(Money total, int participants) {
        if(participants <= 0)
            throw new IllegalArgumentException("Participants must be > 0.");

        long totalCents = total.getAmount();
        long baseShare = totalCents / participants;
        long remainder = totalCents % participants;

        List<Money> shares = new ArrayList<>();
        for (int i = 0; i < participants; i++){
            long share = baseShare + (i < remainder ? 1 : 0);
            shares.add(new Money(share, total.getCurrency()));
        }

        return shares;
    }
}
