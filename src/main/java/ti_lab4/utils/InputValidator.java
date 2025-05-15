package ti_lab4.utils;

public class InputValidator {
    private boolean isPrime(int n) {
        if (n <= 1) return false;
        if (n <= 3) return true;

        if (n % 2 == 0 || n % 3 == 0) return false;

        for (int i = 5; i <= Math.sqrt(n); i++) {
            if (n % i == 0 || n % (i + 2) == 0) return false;
        }
        return true;
    }

    private void validateQ(int q) throws IllegalArgumentException {
        if (!isPrime(q)) {
            throw new IllegalArgumentException("Число q не простое");
        }
    }

    private void validateP(int q, int p) throws IllegalArgumentException {
        if (!isPrime(p)) {
            throw new IllegalArgumentException("Число p не простое");
        }
        if ((p - 1) % q != 0) {
            throw new IllegalArgumentException("q не является делителем p - 1");
        }
    }

    private void validateH(int p, int h) throws IllegalArgumentException {
        if (h <= 1 || h >= p - 1) {
            throw new IllegalArgumentException("h не из интервала (1, p−1)");
        }
    }

    private void validateX(int q, int x) throws IllegalArgumentException {
        if (x <= 0 || x >= q) {
            throw new IllegalArgumentException("x не из интервала (0, q)");
        }
    }

    private void validateK(int q, int k) throws IllegalArgumentException {
        if (k <= 0 || k >= q) {
            throw new IllegalArgumentException("k не из интервала (0, q)");
        }
    }

    public void validateAll(int q, int p, int h, int x, int k) throws IllegalArgumentException {
        validateQ(q);
        validateP(q, p);
        validateH(p, h);
        validateX(q, x);
        validateK(q, k);
    }

    public void validateRAndS(int r, int s) throws IllegalArgumentException {
        if (r == 0 && s == 0) {
            throw new IllegalArgumentException("r и s получились равным 0. Попробуйте другие числа");
        }
        if (r == 0) {
            throw new IllegalArgumentException("r получилось равным 0. Попробуйте другие числа");
        }
        if (s == 0) {
            throw new IllegalArgumentException("s получилось равным 0. Попробуйте другие числа");
        }
    }
}
