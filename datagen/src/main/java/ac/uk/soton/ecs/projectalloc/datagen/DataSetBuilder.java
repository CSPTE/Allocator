package ac.uk.soton.ecs.projectalloc.datagen;

/**
 * gdp-projectalloc - Developed by Lewes D. B. (Boomclaw). All rights reserved 2023.
 */
public class DataSetBuilder {

    protected int numStudents = Integer.MIN_VALUE;
    protected int numSupervisors = Integer.MIN_VALUE;
    protected int perc1st = Integer.MIN_VALUE;
    protected int perc21 = Integer.MIN_VALUE;
    protected int perc22 = Integer.MIN_VALUE;
    protected int perc3rd = Integer.MIN_VALUE;
    protected int percLT3rd = Integer.MIN_VALUE;
    protected int stuPercComp = Integer.MIN_VALUE;
    protected int stuPercAi = Integer.MIN_VALUE;
    protected int stuPercElec = Integer.MIN_VALUE;
    protected int stuPercCS = Integer.MIN_VALUE;
    protected int stuPercSE = Integer.MIN_VALUE;
    protected int supPercComp = Integer.MIN_VALUE;
    protected int supPercAi = Integer.MIN_VALUE;
    protected int supPercElec = Integer.MIN_VALUE;
    protected int supPercCS = Integer.MIN_VALUE;
    protected int supPercSE = Integer.MIN_VALUE;
    protected int minSupervisees = Integer.MIN_VALUE;
    protected int maxSupervisees = Integer.MIN_VALUE;
    protected int numInterests = Integer.MIN_VALUE;
    protected int numSelectedInterests = Integer.MIN_VALUE;
    protected int minChildren = Integer.MIN_VALUE;
    protected int maxChildren = Integer.MIN_VALUE;

    public DataSetBuilder() {

    }

    public DataSetBuilder numStudents(int numStudents) {
        this.numStudents = numStudents;
        return this;
    }

    public DataSetBuilder numSupervisors(int numSupervisors) {
        this.numSupervisors = numSupervisors;
        return this;
    }

    public DataSetBuilder perc1st(int perc1st) {
        this.perc1st = perc1st;
        return this;
    }

    public DataSetBuilder perc21(int perc21) {
        this.perc21 = perc21;
        return this;
    }

    public DataSetBuilder perc22(int perc22) {
        this.perc22 = perc22;
        return this;
    }

    public DataSetBuilder perc3rd(int perc3rd) {
        this.perc3rd = perc3rd;
        return this;
    }

    public DataSetBuilder percLT3rd(int percLT3rd) {
        this.percLT3rd = percLT3rd;
        return this;
    }

    public DataSetBuilder stuPercComp(int stuPercComp) {
        this.stuPercComp = stuPercComp;
        return this;
    }

    public DataSetBuilder stuPercAi(int stuPercAi) {
        this.stuPercAi = stuPercAi;
        return this;
    }

    public DataSetBuilder stuPercElec(int stuPercElec) {
        this.stuPercElec = stuPercElec;
        return this;
    }

    public DataSetBuilder stuPercCS(int stuPercCS) {
        this.stuPercCS = stuPercCS;
        return this;
    }

    public DataSetBuilder stuPercSE(int stuPercSE) {
        this.stuPercSE = stuPercSE;
        return this;
    }

    public DataSetBuilder supPercComp(int supPercComp) {
        this.supPercComp = supPercComp;
        return this;
    }

    public DataSetBuilder supPercAi(int supPercAi) {
        this.supPercAi = supPercAi;
        return this;
    }

    public DataSetBuilder supPercElec(int supPercElec) {
        this.supPercElec = supPercElec;
        return this;
    }

    public DataSetBuilder supPercCS(int supPercCS) {
        this.supPercCS = supPercCS;
        return this;
    }

    public DataSetBuilder supPercSE(int supPercSE) {
        this.supPercSE = supPercSE;
        return this;
    }

    public DataSetBuilder minSupervisees(int minSupervisees) {
        this.minSupervisees = minSupervisees;
        return this;
    }

    public DataSetBuilder maxSupervisees(int maxSupervisees) {
        this.maxSupervisees = maxSupervisees;
        return this;
    }

    public DataSetBuilder numInterests(int numInterests) {
        this.numInterests = numInterests;
        return this;
    }

    public DataSetBuilder numSelectedInterests(int numSelectedInterests) {
        this.numSelectedInterests = numSelectedInterests;
        return this;
    }

    public DataSetBuilder minChildren(int minChildren) {
        this.minChildren = minChildren;
        return this;
    }

    public DataSetBuilder maxChildren(int maxChildren) {
        this.maxChildren = maxChildren;
        return this;
    }

    public DataSet build() {
        DataSetFactory dataSetFactory = new DataSetFactory();

        return dataSetFactory.genDataSet(this);
    }
}
