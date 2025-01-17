/*
 * Copyright (c) 2021 The Hydra authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package be.ugent.zeus.hydra.feed.operations;

import android.os.Bundle;
import androidx.annotation.NonNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.feed.HomeFeedRequest;
import be.ugent.zeus.hydra.feed.cards.Card;

/**
 * An operation that adds items to the home feed.
 *
 * @author Niko Strijbol
 */
class RequestOperation implements FeedOperation {

    private final HomeFeedRequest request;

    RequestOperation(HomeFeedRequest request) {
        this.request = request;
    }

    /**
     * This methods removes all card instances of this operation's card type, performs the request and adds the results
     * back to the list.
     * <p>
     * This means that while the cards may be logically equal, they will not be the same instance.
     *
     * @param current The current cards.
     * @return The updates cards.
     */
    @NonNull
    @Override
    public Result<List<Card>> transform(Bundle args, final List<Card> current) {

        // Filter existing cards away.
        Stream<Card> temp = current.stream()
                .filter(c -> c.getCardType() != request.getCardType());

        return request.execute(args).map(homeCardStream ->
                Stream.concat(temp, homeCardStream).sorted().collect(Collectors.toList())
        );
    }

    @Override
    public int getCardType() {
        return request.getCardType();
    }

    @Override
    public String toString() {
        return "REQUEST -> Card Type " + request.getCardType();
    }
}
