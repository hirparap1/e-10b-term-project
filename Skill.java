enum SkillName {
    OVERALL, ATTACK, DEFENCE, STRENGTH,
    HITPOINTS, RANGED, PRAYER, MAGIC,
    COOKING, WOODCUTTING, FLETCHING,
    FISHING, FIREMAKING, CRAFTING, SMITHING,
    MINING, HERBLORE, AGILITY, THEIVING, SLAYER,
    FARMING, RUNECRAFT, HUNTER, CONSTRUCTION
}

class Skill {
    private SkillName name;
    private int level;
    private int experience;
    private int rank;

    public Skill() {
        this.name = SkillName.OVERALL;
        this.level = 32;
        this.experience = 1154;
        this.rank = 1;
    }

    public Skill(SkillName name, int level, int experience, int rank) {
        this.name = name;
        this.level = level;
        this.experience = experience;
        this.rank = rank;
    }

    public String toString() {
        String formattedString = String.format(
            "%-16s%-8d%-16d%d",
            this.name,
            this.level,
            this.experience,
            this.rank
        );
        System.out.println("TESTING: " + formattedString);

        return formattedString;
    }
}
