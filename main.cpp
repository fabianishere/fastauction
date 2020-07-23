#include "fastauction.h"

int main(int argc, char **argv)
{
    if (argc < 2) {
        printf("usage: %s file\n", argv[0]);
        return 1;
    }

    struct fastauction::Instance instance = fastauction::read_instance(argv[1]);
    printf("%d\n", fastauction::solve_revenue(instance));
    return 0;
}