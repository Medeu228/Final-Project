package medeus.finalproject.Battle;

public interface Combatant {

    String getName();

    int getHP();

    int getAttack();

    void takeDamage(int damage);

    boolean isAlive();
}
