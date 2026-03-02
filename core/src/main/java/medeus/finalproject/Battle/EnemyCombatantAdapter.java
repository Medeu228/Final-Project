package medeus.finalproject.Battle;

import medeus.finalproject.Entities.EnemyAbstract;
import medeus.finalproject.Battle.Combatant;

public class EnemyCombatantAdapter implements Combatant {

    private EnemyAbstract enemy;

    public EnemyCombatantAdapter(EnemyAbstract enemy) {
        this.enemy = enemy;
    }

    @Override
    public String getName() {
        return enemy.getClass().getSimpleName();
    }

    @Override
    public int getHP() {
        return enemy.getHp();
    }

    @Override
    public int getAttack() {
        return enemy.getAttack();
    }

    @Override
    public void takeDamage(int damage) {
        enemy.takeDamage(damage);
    }

    @Override
    public boolean isAlive() {
        return enemy.isAlive();
    }
}
