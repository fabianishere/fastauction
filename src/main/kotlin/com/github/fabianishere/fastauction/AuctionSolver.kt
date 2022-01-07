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

/**
 * An interface for solving auctions with budget constraints [1].
 *
 * [1] Hagerup, T., & Katajainen, J. (Eds.). (2004). Algorithm Theory - SWAT 2004. Lecture Notes in Computer Science.
 */
public interface AuctionSolver {
    /**
     * The name of the solver implementation.
     */
    public val name: String

    /**
     * Solve the specified [instance] and compute the maximum revenue for the seller.
     *
     * @param instance The problem instance to solve.
     * @return The maximum revenue for the seller.
     */
    public fun solve(instance: AuctionInstance): Int
}