/*
 * Copyright (c) 2022 Fabian Mastenbroek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.github.fabianishere.fastauction

import java.util.Objects

/**
 * An instance of the NP-hard auction problem with budget constraints.
 *
 * @property bidders The number of bidders.
 * @property items The number of items.
 * @property budgets Budget constraint of each bidder.
 * @property bids The bids of each bidder for each item.
 */
public data class AuctionInstance(val bidders: Int, val items: Int, val budgets: IntArray, val bids: Array<IntArray>) {
    init {
        require(bidders > 0) { "Auction must have at least one bidder" }
        require(budgets.size == bidders) { "Not enough budget constraints specified" }
        require(bids.size == bidders) { "Not enough bids specified" }
        require(items > 0) { "Auction must have at least one item" }
        require(bids.all { it.size == items }) { "Not enough bids specified for one user" }
    }

    override fun equals(other: Any?): Boolean = other is AuctionInstance
            && bidders == other.bidders
            && items == other.items
            && budgets.contentEquals(other.budgets)
            && bids.contentDeepEquals(other.bids)

    override fun hashCode(): Int = Objects.hash(bidders, items, budgets.contentHashCode(), bids.contentDeepHashCode())
}