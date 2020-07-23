/*
 * Copyright (c) 2020 Fabian Mastenbroek
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

#ifndef FASTAUCTION_H
#define FASTAUCTION_H

#include <vector>

namespace fastauction {
    /**
     * Represents an instance of the NP-hard auction problem with budget
     * constraints.
     */
    struct Instance {
        /**
         *  The number of bidders.
         */
        int bidders;

        /**
         * The number of items.
         */
        int items;

        /**
         * The budget constraint of each bidder <code>i</code>.
         */
        std::vector<int> budget;

        /**
         * The bid of each bidder <code>i</code> to item <code>j</code>.
         */
        std::vector<int> bids;

        /**
         * Construct an instance of an action problem with budget constraints.
         *
         * @param bidders The number of bidders in the problem.
         * @param items The number of items in the auction.
         * @param budgets The budget constraint of each bidder.
         * @param bids The bids of each bidder for each item.
         */
        Instance(int bidders, int items, std::vector<int> budgets,
                        std::vector<int> bids) noexcept;
    };

    /**
     * Read the instance at the specified path.
     *
     * @param path The path at which the instance specification is located.
     * @return The instance that has been read.
     */
    struct Instance read_instance(const char *path);

    /**
     * Solve the auction instance and return the optimal (maximum) seller
     * revenue.
     *
     * @param instance The instance to solve the seller revenue for.
     * @return The maximum revenue that the seller can obtain in the auction.
     */
    int solve_revenue(struct Instance &instance) noexcept;
}

#endif /* FASTAUCTION_H */
