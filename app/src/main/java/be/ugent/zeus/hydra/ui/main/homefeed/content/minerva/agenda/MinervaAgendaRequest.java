package be.ugent.zeus.hydra.ui.main.homefeed.content.minerva.agenda;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.database.minerva2.RepositoryFactory;
import be.ugent.zeus.hydra.domain.models.minerva.AgendaItem;
import be.ugent.zeus.hydra.domain.repository.AgendaItemRepository;
import be.ugent.zeus.hydra.repository.requests.Result;
import be.ugent.zeus.hydra.ui.main.homefeed.HomeFeedRequest;
import be.ugent.zeus.hydra.ui.main.homefeed.content.HomeCard;
import java8.util.stream.Collectors;
import java8.util.stream.Stream;
import java8.util.stream.StreamSupport;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZonedDateTime;

import java.util.List;
import java.util.Map;

/**
 * Request for Minerva agenda items.
 *
 * @author Niko Strijbol
 */
public class MinervaAgendaRequest implements HomeFeedRequest {

    private final AgendaItemRepository dao;

    public MinervaAgendaRequest(Context context) {
        this.dao = RepositoryFactory.getAgendaItemRepository(context);
    }

    @NonNull
    @Override
    public Result<Stream<HomeCard>> performRequest(Bundle args) {

        ZonedDateTime now = ZonedDateTime.now();
        // Only display things up to 3 weeks from now.
        ZonedDateTime oneMonth = now.plusWeeks(3);
        //Note: in real Java 8 streams, we could concat the operations below.
        //Sort the items per day
        Map<LocalDate, List<AgendaItem>> perDay = StreamSupport.stream(dao.getBetween(now, oneMonth))
                        .collect(Collectors.groupingBy(AgendaItem::getLocalStartDate));

        //Convert it to a view
        return Result.Builder.fromData(StreamSupport.stream(perDay.entrySet())
                .map(e -> new MinervaAgendaCard(e.getKey(), e.getValue())));
    }

    @Override
    public int getCardType() {
        return HomeCard.CardType.MINERVA_AGENDA;
    }
}