package gg.skytils.skytilsmod.utils;

public final class CPSDelay {
    private final TimerUtils timerUtils = new TimerUtils(true);

    public boolean shouldAttack(double cps) {
        double aps = 20 / cps;
        return timerUtils.hasReached(50 * aps);
    }

    public void reset() {
        timerUtils.reset();
    }
}
