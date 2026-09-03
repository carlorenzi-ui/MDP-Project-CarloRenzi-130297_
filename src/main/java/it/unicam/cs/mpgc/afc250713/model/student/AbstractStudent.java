package it.unicam.cs.mpgc.afc250713.model.student;

import java.util.Map;

import it.unicam.cs.mpgc.afc250713.model.challenge.AbstractRival;
import it.unicam.cs.mpgc.afc250713.model.challenge.ChallengeStats;
import it.unicam.cs.mpgc.afc250713.model.item.Item;
import it.unicam.cs.mpgc.afc250713.model.item.ItemConsumer;
import it.unicam.cs.mpgc.afc250713.model.item.Appunti;
import it.unicam.cs.mpgc.afc250713.model.resource.ResourceCollector;

/**
 * Base logic for playable student characters.
 * Logic is decoupled into Zaino (backpack), AcademicProgress, FatigueSystem,
 * BudgetSystem and StudyEquipmentManager, mirroring the Single Responsibility
 * Principle.
 */
public abstract class AbstractStudent extends AbstractRival implements ResourceCollector, ItemConsumer {
    public static final int MAX_MORALE = 100;

    private final Zaino zaino;
    private final AcademicProgress academicProgress;
    private final FatigueSystem fatigueSystem;
    private final BudgetSystem budgetSystem;
    private final StudyEquipmentManager studyEquipmentManager;

    public AbstractStudent(final ChallengeStats stats, final int startingBudget) {
        super(stats);
        this.zaino = new Zaino(support);
        this.academicProgress = new AcademicProgress(support);
        this.fatigueSystem = new FatigueSystem(support, () -> this.setMorale(0));
        this.budgetSystem = new BudgetSystem(support, () -> this.setMorale(0), startingBudget);
        this.studyEquipmentManager = new StudyEquipmentManager(support);
    }

    @Override
    public StudyEquipmentManager getStudyEquipment() {
        return this.studyEquipmentManager;
    }

    @Override
    public int getMaxMorale() {
        return MAX_MORALE;
    }

    @Override
    public void addItem(final Item item, final int amount) {
        this.zaino.addItem(item, amount);
    }

    public boolean consumeItem(final Item item, final int amount) {
        return this.zaino.consumeItem(item, amount);
    }

    public void useItem(Item item) {
        if (!consumeItem(item, 1)) {
            throw new IllegalStateException("Non hai " + item.getName() + " nello zaino.");
        }
        item.use(this);
    }

    @Override
    public void restoreMorale(int amount) {
        int newMorale = Math.min(MAX_MORALE, getMorale() + amount);
        setMorale(newMorale);
    }

    @Override
    public void modifyFatigue(int amount) {
        this.fatigueSystem.modifyFatigue(amount);
    }

    public void sufferExamSession() {
        this.fatigueSystem.sufferExamSession();
    }

    public void addFatigue(int amount) {
        this.fatigueSystem.addFatigue(amount);
    }

    @Override
    public int getFatigue() {
        return this.fatigueSystem.getFatigue();
    }

    public void setFatigue(int fatigue) {
        this.fatigueSystem.setFatigue(fatigue);
    }

    public Map<Item, Integer> getZainoContents() {
        return this.zaino.getItems();
    }

    public void clearZaino() {
        this.zaino.clear();
    }

    public void setItemForce(Item item, int amount) {
        this.zaino.setItemForce(item, amount);
    }

    public int getCfu() {
        return this.academicProgress.getCfu();
    }

    public void setCfu(int cfu) {
        this.academicProgress.setCfu(cfu);
    }

    public int getAnnoFuoriCorso() {
        return this.academicProgress.getAnnoFuoriCorso();
    }

    public void setAnnoFuoriCorso(int anno) {
        this.academicProgress.setAnnoFuoriCorso(anno);
    }

    public double getMedia() {
        return this.academicProgress.getMedia();
    }

    public void setMedia(double media) {
        this.academicProgress.setMedia(media);
    }

    public int getEsamiSuperati() {
        return this.academicProgress.getEsamiSuperati();
    }

    public void setEsamiSuperati(int esami) {
        this.academicProgress.setEsamiSuperati(esami);
    }

    public void registerPassedExam(int cfuGained, int voto) {
        this.academicProgress.registerPassedExam(cfuGained, voto);
    }

    public boolean hasGraduated() {
        return this.academicProgress.hasGraduated();
    }

    public int getBudget() {
        return this.budgetSystem.getBudget();
    }

    public void setBudget(int budget) {
        this.budgetSystem.setBudget(budget);
    }

    public void modifyBudget(int amount) {
        this.budgetSystem.modifyBudget(amount);
    }

    public void payWeeklyUpkeep() {
        this.budgetSystem.payWeeklyUpkeep();
    }

    public int getGruppoStudioShield() {
        return this.studyEquipmentManager.getBuffValue(StudyBuffType.GRUPPO_STUDIO);
    }

    public void setGruppoStudioShield(int shield) {
        this.studyEquipmentManager.addBuff(StudyBuffType.GRUPPO_STUDIO, shield);
    }

    public boolean isAppuntiEquipped() {
        return this.studyEquipmentManager.hasBuff(StudyBuffType.APPUNTI);
    }

    public void setAppuntiEquipped(boolean equipped) {
        if (equipped) {
            this.studyEquipmentManager.addBuff(StudyBuffType.APPUNTI, Appunti.DEFAULT_DURABILITY);
        } else {
            this.studyEquipmentManager.removeBuff(StudyBuffType.APPUNTI);
        }
    }

    public int getAppuntiDurability() {
        return this.studyEquipmentManager.getBuffValue(StudyBuffType.APPUNTI);
    }

    public void setAppuntiDurability(int durability) {
        if (durability > 0) {
            this.studyEquipmentManager.addBuff(StudyBuffType.APPUNTI, durability);
        } else {
            this.studyEquipmentManager.removeBuff(StudyBuffType.APPUNTI);
        }
    }

    @Override
    public int getPower() {
        return this.studyEquipmentManager.calculatePower(super.getPower());
    }

    @Override
    public void engage(it.unicam.cs.mpgc.afc250713.model.challenge.Vulnerable target) {
        super.engage(target);
        this.studyEquipmentManager.onExamAttempt();
    }

    @Override
    public void loseMorale(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Pressure amount must be greater than zero.");
        }
        int remaining = this.studyEquipmentManager.absorbStress(amount);
        if (remaining > 0) {
            super.loseMorale(remaining);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractStudent that = (AbstractStudent) o;
        return getMorale() == that.getMorale() && getFatigue() == that.getFatigue() &&
                getCfu() == that.getCfu() && getAnnoFuoriCorso() == that.getAnnoFuoriCorso() &&
                getBudget() == that.getBudget();
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(getMorale(), getFatigue(), getCfu(), getAnnoFuoriCorso(), getBudget());
    }
}
