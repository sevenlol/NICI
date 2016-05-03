package tw.gov.ey.nici.utils;

public class PaginationUtil {
    /**
     *  for simplicity, skip must be divisible by limit
     */
    public static int getPageIndex(int skip, int limit) {
        check(skip, limit);
        return (skip / limit) + 1;
    }

    public static void check(int skip, int limit) {
        if (skip < 0 || limit < 0) {
            throw new IllegalArgumentException();
        }
        if (skip % limit != 0) {
            throw new IllegalArgumentException();
        }
    }
}
