#include "fastauction.h"

#include <utility>
#include <vector>
#include <fstream>
#include <iostream>
#include <cstdint>

struct fastauction::Instance fastauction::read_instance(const char *path)
{
    std::ifstream is(path);

    int n, k;
    is >> n;
    is >> k;

    std::vector<int> d(n);
    std::vector<int> b(n * k);

    for (int i = 0; i < n; i++) {
        is >> d[i];
    }

    for (int i = 0; i < n; i++) {
        for (int j = 0; j < k; j++) {
            is >> b[i * n + j];
        }
    }

    return fastauction::Instance(n, k, d, b);
}

fastauction::Instance::Instance(int bidders, int items,
                                std::vector<int> budgets,
                                std::vector<int> bids) noexcept:
        bidders(bidders), items(items),
        budget(std::move(budgets)), bids(std::move(bids)) {}

/**
 * Determine the next set bit in the bit string <code>s</code> from position
 * <code>i</code>.
 *
 * @param s The bit string to find the next set bit in.
 * @param i The position in the bit string to start searching from.
 * @return The position in the bit string where the next bit is set or
 * <code>-1</code> if no remaining bits are set.
 */
static int nextSetBit(std::uint32_t s, std::uint32_t i)
{
    constexpr std::uint32_t WORD_MASK = 0xffffffff;

    std::uint32_t word = s & (WORD_MASK << i);
    if (word != 0)
        return __builtin_ctz(word);
    else
        return -1;
}

/**
 * Compute the maximal revenue from some bidder for the given allocation <code>u</code>.
 *
 * @param d The budget constraint set by the bidder.
 * @param b The bids of the bidder.
 * @param u The allocation for which to compute the maximal revenue represented as bit string.
 * @return The maximal revenue when allocating the items to this bidder.
 */
static int revenue(int d, const int *b, std::uint32_t u) noexcept
{
    int r = 0;

    // Find the positions of the set bits and sum the bids of these positions.
    for (int j = nextSetBit(u, 0); j >= 0; j = nextSetBit(u, j + 1)) {
        r += b[j];
    }

    return std::min(r, d);
}

/**
 * Compute the maximal revenue from some bidder for each possible allocation of
 * <code>k</code> items in the auction (the power set of items).
 *
 * We represent an allocation of items as a bit string <code>u</code> where the
 * ith position in the bit string represents the allocation of the ith item in
 * the auction.
 *
 * @param S A vector of size <code>2^k</code> to which the results are written.
 * @param d The budget constraint set by the bidder.
 * @param b The bids of the bidder.
 */
static void generate(std::vector<int> &S, int n, int d, const int *b) noexcept
{
    // Note: since allocations are represented as bit strings, we can quickly enumerate
    // the power set of allocations by incrementing the bit string by one.
    for (uint32_t u = 1; u < n; u++) {
        S[u] = revenue(d, b, u);
    }
}

/**
 * Merge two sets of allocations while maximizing the total revenue of each allocation.
 *
 * @param Sr An array of size <code>2^</code> to which the revenues are written.
 * @param S The initial set of allocations.
 * @param Sp The set of allocations to merge with.
 */
static void
merge(std::vector<int> &Sr, int n, std::vector<int> &S,
      std::vector<int> &Sp) noexcept
{
    // Our merging strategy works as follows:
    // 1. Enumerate each possible allocation `u` in the first set
    // 2. Determine the optimal assignment of this allocation by calculating the revenue
    //    of each possible assignment to either `S` or `Sp`.
    for (std::uint32_t u = 1; u < n; u++) {
        // Note: we use bit manipulations to obtain the permutations of toggled bits in `u`.
        // Suppose we have the allocation 0101. We want to obtain every possible
        // assignment of this allocation with `S` and `Sp`. That is:
        // 1. 0101 (S), 0000 (Sp)
        // 2. 0100 (S), 0001 (Sp)
        // 3. 0001 (S), 0100 (Sp)
        // 4. 0000 (S), 0101 (Sp)
        int r = S[u];

        // Initial case: all items are assigned to `S`
        std::uint32_t m = u;
        do {
            r = std::max(r, S[m] + Sp[~m & u]);
            m = (m - 1) & u;
        } while (m != u);

        Sr[u] = r;
    }
}

int fastauction::solve_revenue(struct Instance &instance) noexcept
{
    int n = instance.bidders;
    int k = instance.items;
    const auto &d = instance.budget;
    const auto &b = instance.bids;

    // Note: we pre-allocate to contain the revenues (S, Sp) and the result of merging (Sr).
    // It is cleaner to have `generate` and `merge` instantiate this array themselves, but this
    // has a significant effect on performance.
    std::vector<int> S(1 << k);
    std::vector<int> Sp(1 << k);
    std::vector<int> Sr(1 << k);

    generate(S, n, d[0], b.data());

    for (int i = 1; i < n; i++) {
        generate(Sp, n, d[i], b.data() + i * n);
        merge(Sr, n, S, Sp);

        // Swap `S` and `Sr`: we need to re-use the merging results in the next round.
        S.swap(Sr);
    }

    return S[n - 1];
}