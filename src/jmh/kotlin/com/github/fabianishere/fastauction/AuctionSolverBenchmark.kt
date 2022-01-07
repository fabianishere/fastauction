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

import org.openjdk.jmh.annotations.*
import java.io.File


/**
 * Benchmark suite for the [AuctionSolver] instances.
 */
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
@State(Scope.Benchmark)
class AuctionSolverBenchmark {
    /**
     * The instance to solve.
     */
    private lateinit var instance: AuctionInstance

    @Param("20")
    private var bidders = 0

    @Param("10")
    private var items = 0

    @Param("400")
    private var dmax = 0

    @Param("0")
    private var i = 0

    /**
     * Set up the benchmark trial.
     */
    @Setup(Level.Trial)
    fun setUp() {
        instance = READER.read(File("examples/n_%d_k_%d_dmax_%d_%d.txt".format(bidders, items, dmax, i)))
    }


    // @Benchmark
    fun solveDPA(): Int {
        return AuctionSolverDPA(0.03).solve(instance)
    }

    @Benchmark
    fun solveExact(): Int {
        return AuctionSolverExact().solve(instance)
    }

    @Benchmark
    fun solveExactSIMD(): Int {
        return AuctionSolverExactSIMD().solve(instance)
    }

    companion object {
        private val READER = AuctionInstanceReader()
    }
}