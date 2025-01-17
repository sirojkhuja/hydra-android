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

package be.ugent.zeus.hydra.common.reporting;

/**
 * Provides basic logging constants for events. These should be available in all implementations. If not available, the
 * method may return {@code null} meaning the event will not be logged.
 *
 * @author Niko Strijbol
 * @implNote These are inspired by the Firebase implementation.
 */
public interface BaseEvents {

    /**
     * @return The parameter implementation.
     */
    Params params();

    /**
     * Login event. Apps with a login feature can report this event to signify that a user has logged in.<br> Params:
     * <ul>
     * <li>{@link Params#method()}</li>
     * </ul>
     */
    String login();

    /**
     * Search event. Apps that support search features can use this event to contextualize search operations by
     * supplying the appropriate, corresponding parameters. This event can help you identify the most popular content in
     * your app. Params:
     * <ul>
     * <li>{@link Params#searchTerm()}</li>
     * </ul>
     */
    String search();

    /**
     * Select Content event. This general purpose event signifies that a user has selected some content of a certain
     * type in an app. The content can be any object in your app. This event can help you identify popular content and
     * categories of content in your app. Params:
     * <ul>
     * <li>{@link Params#contentType()}</li>
     * <li>{@link Params#itemId()}</li>
     * </ul>
     */
    String selectContent();

    /**
     * Share event. Apps with social features can log the Share event to identify the most viral content. Params:
     * <ul>
     * <li>{@link Params#contentType()}</li>
     * <li>{@link Params#itemId()}</li>
     * <li>{@link Params#method()}</li>
     * </ul>
     */
    String share();

    /**
     * Tutorial Begin event. This event signifies the start of the on-boarding process in your app.
     */
    String tutorialBegin();

    /**
     * Tutorial End event. Use this event to signify the user's completion of your app's on-boarding process.
     */
    String tutorialComplete();

    /**
     * View Item event. This event signifies that some content was shown to the user. This content may be a product, a
     * webpage or just a simple image or text. Use the appropriate parameters to contextualize the event. Use this event
     * to discover the most popular items viewed in your app. Params:
     * <ul>
     * <li>{@link Params#itemId()}</li>
     * <li>{@link Params#itemName()}</li>
     * <li>{@link Params#itemCategory()}</li>
     * </ul>
     * <p>
     * todo the difference between this and {@link #selectContent()}. This is more when something is shown to the user,
     *   the other when something is selected by the user (can be button, photo, image, etc.)
     *   Perhaps: when a visitor clicks on an article, we log SELECT_ITEM. When a visitor views an article,
     *   we use VIEW_ITEM.
     */
    String viewItem();

    /**
     * A custom event to report that the user has dismissed a card from the home feed.
     * <p>
     * Params:
     * <ul>
     *     <li>{@link Params#dismissalType()}</li>
     *     <li>{@link Params#cardType()}</li>
     *     <li>{@link Params#cardIdentifier()}</li>
     * </ul>
     */
    String cardDismissal();

    interface Params {
        /**
         * A particular approach used in an operation; for example, "facebook" or "email" in the context of a sign_up or
         * login event (String).
         */
        String method();

        /**
         * The search string/keywords used (String).
         */
        String searchTerm();

        /**
         * Type of content selected (String).
         */
        String contentType();

        /**
         * Item ID (String).
         */
        String itemId();

        /**
         * Item name (String).
         */
        String itemName();

        /**
         * Item category (String).
         */
        String itemCategory();

        /**
         * Dismissal type (String)
         */
        String dismissalType();

        /**
         * Card type (int)
         */
        String cardType();

        /**
         * Card identifier (String)
         */
        String cardIdentifier();
    }
}
