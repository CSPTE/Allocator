package ac.uk.soton.ecs.projectalloc.datagen;

import ac.uk.soton.ecs.projectalloc.ChosenStudentData;
import ac.uk.soton.ecs.projectalloc.ChosenSupervisorData;
import ac.uk.soton.ecs.projectalloc.Node;
import ac.uk.soton.ecs.projectalloc.Student;
import ac.uk.soton.ecs.projectalloc.datagen.DataSet;
import ac.uk.soton.ecs.projectalloc.datagen.DataSetFactory;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataSetFactoryTest {

    @Nested
    @DisplayName("Correct Input Testing")
    class DataSetInputTesting {

        @Test
        public void test0Students() {
            DataSetFactory dsFactory = new DataSetFactory();
            Assert.assertThrows(IllegalArgumentException.class, () -> {
                DataSet ds = dsFactory.genDataSet(0, 50, 100, 0, 0, 0, 0, 100, 0, 0, 0, 0, 100, 0, 0, 0, 0, 1, 2, 50, 20, 3, 5);
            });
        }

        @Test
        public void test0Supervisees() {
            DataSetFactory dsFactory = new DataSetFactory();
            Assert.assertThrows(IllegalArgumentException.class, () -> {
                DataSet ds = dsFactory.genDataSet(100, 0, 100, 0, 0, 0, 0, 100, 0, 0, 0, 0, 100, 0, 0, 0, 0, 1, 2, 50, 20, 3, 5);
            });
        }

        @Test
        public void testStudentSpecialismLessThan0() {
            DataSetFactory dsFactory = new DataSetFactory();
            Assert.assertThrows(IllegalArgumentException.class, () -> {
                DataSet ds = dsFactory.genDataSet(100, 100, 100, 0, 0, 0, 0, 150, -50, 0, 0, 0, 100, 0, 0, 0, 0, 1, 2, 50, 20, 3, 5);
            });
        }

        @Test
        public void testStudentSpecialismDistGreaterThan100() {
            DataSetFactory dsFactory = new DataSetFactory();
            Assert.assertThrows(IllegalArgumentException.class, () -> {
                DataSet ds = dsFactory.genDataSet(100, 100, 100, 0, 0, 0, 0, 100, 1, 0, 0, 0, 100, 0, 0, 0, 0, 1, 2, 50, 20, 3, 5);
            });
        }

        @Test
        public void testStudentSpecialismDistLessThan100() {
            DataSetFactory dsFactory = new DataSetFactory();
            Assert.assertThrows(IllegalArgumentException.class, () -> {
                DataSet ds = dsFactory.genDataSet(100, 100, 100, 0, 0, 0, 0, 99, 0, 0, 0, 0, 100, 0, 0, 0, 0, 1, 2, 50, 20, 3, 5);
            });
        }

        @Test
        public void testStudentGradeDistGreaterThan100() {
            DataSetFactory dsFactory = new DataSetFactory();
            Assert.assertThrows(IllegalArgumentException.class, () -> {
                DataSet ds = dsFactory.genDataSet(100, 100, 100, 1, 0, 0, 0, 100, 0, 0, 0, 0, 100, 0, 0, 0, 0, 1, 2, 50, 20, 3, 5);
            });
        }

        @Test
        public void testStudentGradeDistLessThan100() {
            DataSetFactory dsFactory = new DataSetFactory();
            Assert.assertThrows(IllegalArgumentException.class, () -> {
                DataSet ds = dsFactory.genDataSet(100, 100, 99, 0, 0, 0, 0, 99, 0, 0, 0, 0, 100, 0, 0, 0, 0, 1, 2, 50, 20, 3, 5);
            });
        }

        @Test
        public void testStudentGradeLessThan0() {
            DataSetFactory dsFactory = new DataSetFactory();
            Assert.assertThrows(IllegalArgumentException.class, () -> {
                DataSet ds = dsFactory.genDataSet(100, 100, 150, -50, 0, 0, 0, 100, 0, 0, 0, 0, 100, 0, 0, 0, 0, 1, 2, 50, 20, 3, 5);
            });
        }

        @Test
        public void testSupervisorSpecialismDistLessThan100() {
            DataSetFactory dsFactory = new DataSetFactory();
            Assert.assertThrows(IllegalArgumentException.class, () -> {
                DataSet ds = dsFactory.genDataSet(100, 100, 100, 0, 0, 0, 0, 100, 0, 0, 0, 0, 99, 0, 0, 0, 0, 1, 2, 50, 20, 3, 5);
            });
        }

        @Test
        public void testSupervisorSpecialismDistGreaterThan100() {
            DataSetFactory dsFactory = new DataSetFactory();
            Assert.assertThrows(IllegalArgumentException.class, () -> {
                DataSet ds = dsFactory.genDataSet(100, 100, 100, 0, 0, 0, 0, 100, 0, 0, 0, 0, 101, 0, 0, 0, 0, 1, 2, 50, 20, 3, 5);
            });
        }

        @Test
        public void testSupervisorSpecialismLessThan0() {
            DataSetFactory dsFactory = new DataSetFactory();
            Assert.assertThrows(IllegalArgumentException.class, () -> {
                DataSet ds = dsFactory.genDataSet(100, 100, 100, 0, 0, 0, 0, 100, 0, 0, 0, 0, 150, -50, 0, 0, 0, 1, 2, 50, 20, 3, 5);
            });
        }

        @Test
        public void testMinSuperviseesGreaterThanMaxSupervisees() {
            DataSetFactory dsFactory = new DataSetFactory();
            Assert.assertThrows(IllegalArgumentException.class, () -> {
                DataSet ds = dsFactory.genDataSet(100, 100, 100, 0, 0, 0, 0, 100, 0, 0, 0, 0, 101, 0, 0, 0, 0, 2, 1, 50, 20, 3, 5);
            });
        }

        @Test
        public void testMinSuperviseesLessThan0() {
            DataSetFactory dsFactory = new DataSetFactory();
            Assert.assertThrows(IllegalArgumentException.class, () -> {
                DataSet ds = dsFactory.genDataSet(100, 100, 100, 0, 0, 0, 0, 100, 0, 0, 0, 0, 101, 0, 0, 0, 0, -2, 1, 50, 20, 2, 5);
            });
        }

        @Test
        public void testMaxSuperviseesLessThan0() {
            DataSetFactory dsFactory = new DataSetFactory();
            Assert.assertThrows(IllegalArgumentException.class, () -> {
                DataSet ds = dsFactory.genDataSet(100, 100, 100, 0, 0, 0, 0, 100, 0, 0, 0, 0, 101, 0, 0, 0, 0, -5, -3, 50, 20, 2, 5);
            });
        }

        @Test
        public void testSelectedInterestsGreaterThanNumInterests() {
            DataSetFactory dsFactory = new DataSetFactory();
            Assert.assertThrows(IllegalArgumentException.class, () -> {
                DataSet ds = dsFactory.genDataSet(100, 100, 100, 0, 0, 0, 0, 100, 0, 0, 0, 0, 101, 0, 0, 0, 0, 2, 1, 50, 70, 3, 5);
            });
        }

        @Test
        public void testMinChildrenGreaterThanMinChildren() {
            DataSetFactory dsFactory = new DataSetFactory();
            Assert.assertThrows(IllegalArgumentException.class, () -> {
                DataSet ds = dsFactory.genDataSet(100, 100, 100, 0, 0, 0, 0, 100, 0, 0, 0, 0, 101, 0, 0, 0, 0, 2, 1, 50, 70, 7, 5);
            });
        }

        @Test
        public void testMinChildrenLessThan0() {
            DataSetFactory dsFactory = new DataSetFactory();
            Assert.assertThrows(IllegalArgumentException.class, () -> {
                DataSet ds = dsFactory.genDataSet(100, 100, 100, 0, 0, 0, 0, 100, 0, 0, 0, 0, 101, 0, 0, 0, 0, 2, 1, 50, 70, -7, 5);
            });
        }

        @Test
        public void testMaxChildrenLessThan0() {
            DataSetFactory dsFactory = new DataSetFactory();
            Assert.assertThrows(IllegalArgumentException.class, () -> {
                DataSet ds = dsFactory.genDataSet(100, 100, 100, 0, 0, 0, 0, 100, 0, 0, 0, 0, 101, 0, 0, 0, 0, 1, 1, 20, 10, -7, -5);
            });
        }

        @Test
        public void testGenGrade23() {
            DataSetFactory dsFactory = new DataSetFactory();
            Assert.assertThrows(IllegalArgumentException.class, () -> {
                Integer grade = dsFactory.genGrade("23");
            });
        }

        @Test
        public void testMoreStudentThanAllocations() {
            DataSetFactory dsFactory = new DataSetFactory();
            Assert.assertThrows(IllegalArgumentException.class, () -> {
                DataSet ds = dsFactory.genDataSet(300, 100, 100, 0, 0, 0, 0, 100, 0, 0, 0, 0, 100, 0, 0, 0, 0, 1, 1, 50, 30, 1, 2);
            });
        }
    }

    @Nested
    @DisplayName("Functionality Testing")
    class DataSetFunctionalityTesting {
        @Test
        public void test385Students385AllDistBellCurve() {
            DataSet ds = new DataSetBuilder()
                    .numStudents(385)
                    .numSupervisors(385)
                    .perc1st(10)
                    .perc21(25)
                    .perc22(30)
                    .perc3rd(25)
                    .percLT3rd(10)
                    .stuPercComp(10)
                    .stuPercAi(25)
                    .stuPercElec(30)
                    .stuPercCS(25)
                    .stuPercSE(10)
                    .supPercComp(10)
                    .supPercAi(25)
                    .supPercElec(30)
                    .supPercCS(25)
                    .supPercSE(10)
                    .minSupervisees(1)
                    .maxSupervisees(2)
                    .numInterests(50)
                    .numSelectedInterests(10)
                    .minChildren(2)
                    .maxChildren(5)
                    .build();

            List<ChosenStudentData> students = ds.getStudents();
            List<ChosenSupervisorData> supervisors = ds.getSupervisors();


            Assert.assertEquals(385,students.size());
            Assert.assertEquals(385,supervisors.size());

        }

        @Test
        public void test385Students385SupervisorsUniGradeDist() {
            DataSet ds = new DataSetBuilder()
                    .numStudents(385)
                    .numSupervisors(385)
                    .perc1st(100)
                    .perc21(0)
                    .perc22(0)
                    .perc3rd(0)
                    .percLT3rd(0)
                    .stuPercComp(100)
                    .stuPercAi(0)
                    .stuPercElec(0)
                    .stuPercCS(0)
                    .stuPercSE(0)
                    .supPercComp(100)
                    .supPercAi(0)
                    .supPercElec(0)
                    .supPercCS(0)
                    .supPercSE(0)
                    .minSupervisees(1)
                    .maxSupervisees(2)
                    .numInterests(50)
                    .numSelectedInterests(10)
                    .minChildren(2)
                    .maxChildren(5)
                    .build();

            List<ChosenStudentData> students = ds.getStudents();
            List<ChosenSupervisorData> supervisors = ds.getSupervisors();

            Assert.assertEquals(385,students.size());
            Assert.assertEquals(385,supervisors.size());
        }

        @Test
        public void test385Students385SupervisorsBellCurveGradeDist() {
            DataSet ds = new DataSetBuilder()
                    .numStudents(385)
                    .numSupervisors(385)
                    .perc1st(10)
                    .perc21(25)
                    .perc22(30)
                    .perc3rd(25)
                    .percLT3rd(10)
                    .stuPercComp(100)
                    .stuPercAi(0)
                    .stuPercElec(0)
                    .stuPercCS(0)
                    .stuPercSE(0)
                    .supPercComp(100)
                    .supPercAi(0)
                    .supPercElec(0)
                    .supPercCS(0)
                    .supPercSE(0)
                    .minSupervisees(1)
                    .maxSupervisees(2)
                    .numInterests(50)
                    .numSelectedInterests(10)
                    .minChildren(2)
                    .maxChildren(5)
                    .build();

            List<ChosenStudentData> students = ds.getStudents();
            List<ChosenSupervisorData> supervisors = ds.getSupervisors();

            Assert.assertEquals(385,students.size());
            Assert.assertEquals(385,supervisors.size());
        }

        @Test
        public void test385Students385SupervisorsBellCurveStuSpecialism() {
            DataSet ds = new DataSetBuilder()
                    .numStudents(385)
                    .numSupervisors(385)
                    .perc1st(100)
                    .perc21(0)
                    .perc22(0)
                    .perc3rd(0)
                    .percLT3rd(0)
                    .stuPercComp(10)
                    .stuPercAi(25)
                    .stuPercElec(30)
                    .stuPercCS(25)
                    .stuPercSE(10)
                    .supPercComp(100)
                    .supPercAi(0)
                    .supPercElec(0)
                    .supPercCS(0)
                    .supPercSE(0)
                    .minSupervisees(1)
                    .maxSupervisees(2)
                    .numInterests(50)
                    .numSelectedInterests(10)
                    .minChildren(2)
                    .maxChildren(5)
                    .build();

            List<ChosenStudentData> students = ds.getStudents();
            List<ChosenSupervisorData> supervisors = ds.getSupervisors();

            Assert.assertEquals(385,students.size());
            Assert.assertEquals(385,supervisors.size());
        }

        @Test
        public void test385Students385SupervisorsBellCurveSupSpecialism() {
            DataSet ds = new DataSetBuilder()
                    .numStudents(385)
                    .numSupervisors(385)
                    .perc1st(100)
                    .perc21(0)
                    .perc22(0)
                    .perc3rd(0)
                    .percLT3rd(0)
                    .stuPercComp(100)
                    .stuPercAi(0)
                    .stuPercElec(0)
                    .stuPercCS(0)
                    .stuPercSE(0)
                    .supPercComp(10)
                    .supPercAi(25)
                    .supPercElec(30)
                    .supPercCS(25)
                    .supPercSE(10)
                    .minSupervisees(1)
                    .maxSupervisees(2)
                    .numInterests(50)
                    .numSelectedInterests(10)
                    .minChildren(2)
                    .maxChildren(5)
                    .build();

            List<ChosenStudentData> students = ds.getStudents();
            List<ChosenSupervisorData> supervisors = ds.getSupervisors();

            Assert.assertEquals(385,students.size());
            Assert.assertEquals(385,supervisors.size());
        }

        @Test
        public void test385Students385Supervisors() {
            DataSet ds = new DataSetBuilder()
                    .numStudents(385)
                    .numSupervisors(385)
                    .perc1st(10)
                    .perc21(25)
                    .perc22(30)
                    .perc3rd(25)
                    .percLT3rd(10)
                    .stuPercComp(100)
                    .stuPercAi(0)
                    .stuPercElec(0)
                    .stuPercCS(0)
                    .stuPercSE(0)
                    .supPercComp(100)
                    .supPercAi(0)
                    .supPercElec(0)
                    .supPercCS(0)
                    .supPercSE(0)
                    .minSupervisees(1)
                    .maxSupervisees(2)
                    .numInterests(50)
                    .numSelectedInterests(10)
                    .minChildren(2)
                    .maxChildren(5)
                    .build();

            List<ChosenStudentData> students = ds.getStudents();
            List<ChosenSupervisorData> supervisors = ds.getSupervisors();

            Assert.assertEquals(385,students.size());
            Assert.assertEquals(385,supervisors.size());
        }

        @Test
        public void testAllParticipants10Interests() {
            DataSet ds = new DataSetBuilder()
                    .numStudents(100)
                    .numSupervisors(400)
                    .perc1st(100)
                    .perc21(0)
                    .perc22(0)
                    .perc3rd(0)
                    .percLT3rd(0)
                    .stuPercComp(100)
                    .stuPercAi(0)
                    .stuPercElec(0)
                    .stuPercCS(0)
                    .stuPercSE(0)
                    .supPercComp(100)
                    .supPercAi(0)
                    .supPercElec(0)
                    .supPercCS(0)
                    .supPercSE(0)
                    .minSupervisees(1)
                    .maxSupervisees(2)
                    .numInterests(50)
                    .numSelectedInterests(10)
                    .minChildren(2)
                    .maxChildren(5)
                    .build();

            boolean all10Interests = true;

            for(ChosenStudentData stu: ds.getStudents()) {
                if(stu.getInterestTree().getAllInterests().size() != 10) {
                    all10Interests = false;
                }
            }


            for(ChosenSupervisorData sup: ds.getSupervisors()) {
                if(sup.getInterestTree().getAllInterests().size() != 10) {
                    all10Interests = false;
                }
            }

            Assert.assertEquals(true,all10Interests);
        }

        @Test
        public void differentParticipantTrees() {
            DataSet ds = new DataSetBuilder()
                    .numStudents(100)
                    .numSupervisors(400)
                    .perc1st(100)
                    .perc21(0)
                    .perc22(0)
                    .perc3rd(0)
                    .percLT3rd(0)
                    .stuPercComp(100)
                    .stuPercAi(0)
                    .stuPercElec(0)
                    .stuPercCS(0)
                    .stuPercSE(0)
                    .supPercComp(100)
                    .supPercAi(0)
                    .supPercElec(0)
                    .supPercCS(0)
                    .supPercSE(0)
                    .minSupervisees(1)
                    .maxSupervisees(2)
                    .numInterests(50)
                    .numSelectedInterests(10)
                    .minChildren(2)
                    .maxChildren(5)
                    .build();

            boolean differentStuTrees = false;
            boolean differentSupTrees = false;

            Node stuTree = null;
            Node supTree = null;

            for(ChosenStudentData stu: ds.getStudents()) {
                if(stuTree == null) {
                    stuTree = stu.getInterestTree();
                } else if(!stuTree.equals(stu.getInterestTree())) {
                    differentStuTrees = true;
                }
            }

            for(ChosenSupervisorData sup: ds.getSupervisors()) {
                if(supTree == null) {
                    supTree = sup.getInterestTree();
                } else if(!stuTree.equals(sup.getInterestTree())) {
                    differentSupTrees = true;
                }
            }

            Assert.assertTrue(differentStuTrees == differentSupTrees == true);
        }

        @Test
        public void testTreeGeneratedHas50Nodes() {
            DataSet ds = new DataSetBuilder()
                    .numStudents(100)
                    .numSupervisors(400)
                    .perc1st(100)
                    .perc21(0)
                    .perc22(0)
                    .perc3rd(0)
                    .percLT3rd(0)
                    .stuPercComp(100)
                    .stuPercAi(0)
                    .stuPercElec(0)
                    .stuPercCS(0)
                    .stuPercSE(0)
                    .supPercComp(100)
                    .supPercAi(0)
                    .supPercElec(0)
                    .supPercCS(0)
                    .supPercSE(0)
                    .minSupervisees(1)
                    .maxSupervisees(2)
                    .numInterests(50)
                    .numSelectedInterests(10)
                    .minChildren(2)
                    .maxChildren(5)
                    .build();

            Assert.assertEquals(50,ds.getSuperInterestTree().getAllNodes().size());
        }

        @Test
        public void testTreeGeneratedHas50Interests() {
            DataSet ds = new DataSetBuilder()
                    .numStudents(100)
                    .numSupervisors(400)
                    .perc1st(100)
                    .perc21(0)
                    .perc22(0)
                    .perc3rd(0)
                    .percLT3rd(0)
                    .stuPercComp(100)
                    .stuPercAi(0)
                    .stuPercElec(0)
                    .stuPercCS(0)
                    .stuPercSE(0)
                    .supPercComp(100)
                    .supPercAi(0)
                    .supPercElec(0)
                    .supPercCS(0)
                    .supPercSE(0)
                    .minSupervisees(1)
                    .maxSupervisees(2)
                    .numInterests(50)
                    .numSelectedInterests(10)
                    .minChildren(2)
                    .maxChildren(5)
                    .build();

            Assert.assertEquals(50,ds.getSuperInterestTree().getAllInterests().size());
        }

        @Test
        public void test100Students() {
            DataSetFactory dsFactory = new DataSetFactory();
            DataSet ds = dsFactory.genDataSet(100, 50, 100, 0, 0, 0, 0, 100, 0, 0, 0, 0, 100, 0, 0, 0, 0, 1, 2, 50, 20, 3, 5);
            Assert.assertEquals(100, ds.getStudents().size());
        }

        @Test
        public void test50Supervisors() {
            DataSetFactory dsFactory = new DataSetFactory();
            DataSet ds = dsFactory.genDataSet(100, 50, 100, 0, 0, 0, 0, 100, 0, 0, 0, 0, 100, 0, 0, 0, 0, 1, 2, 50, 20, 3, 5);
            Assert.assertEquals(50, ds.getSupervisors().size());
        }

        @Test
        public void testGradeDistWithAllOneSpecialismIsCorrect() {
            DataSetFactory dsFactory = new DataSetFactory();
            DataSet ds = dsFactory.genDataSet(100, 50, 30, 40, 30, 0, 0, 100, 0, 0, 0, 0, 100, 0, 0, 0, 0, 1, 2, 50, 20, 3, 5);
            Integer num1st = 0;
            Integer num21 = 0;
            Integer num22 = 0;
            Integer num3rd = 0;
            Integer numLT3rd = 0;

            for(ChosenStudentData stu: ds.getStudents()) {
                if(stu.getGrade() >= 70) {
                    num1st++;
                } else if (stu.getGrade() >= 60) {
                    num21++;
                } else if (stu.getGrade() >= 50) {
                    num22++;
                } else if (stu.getGrade() >= 40) {
                    num3rd++;
                } else {
                    numLT3rd++;
                }
            }

            Assert.assertEquals(List.of(30,40,30,0,0),List.of(num1st,num21,num22,num3rd,numLT3rd));
        }

        @Test
        public void testGradeDistWithMultipleSpecialism() {
            DataSetFactory dsFactory = new DataSetFactory();
            DataSet ds = dsFactory.genDataSet(100, 50, 30, 40, 30, 0, 0, 25, 25, 25, 25, 0, 100, 0, 0, 0, 0, 1, 2, 50, 20, 3, 5);
            Integer num1st = 0;
            Integer num21 = 0;
            Integer num22 = 0;
            Integer num3rd = 0;
            Integer numLT3rd = 0;

            for(ChosenStudentData stu: ds.getStudents()) {
                if(stu.getGrade() >= 70) {
                    num1st++;
                } else if (stu.getGrade() >= 60) {
                    num21++;
                } else if (stu.getGrade() >= 50) {
                    num22++;
                } else if (stu.getGrade() >= 40) {
                    num3rd++;
                } else {
                    numLT3rd++;
                }
            }

            Assert.assertEquals(List.of(30,40,30,0,0),List.of(num1st,num21,num22,num3rd,numLT3rd));
        }

        @Test
        public void testStuSpecialismDistWithAllOneGrade() {
            DataSetFactory dsFactory = new DataSetFactory();
            DataSet ds = dsFactory.genDataSet(100, 50, 100, 0, 0, 0, 0, 25, 25, 25, 25, 0, 100, 0, 0, 0, 0, 1, 2, 50, 20, 3, 5);
            Integer numComp = 0;
            Integer numSE = 0;
            Integer numCS = 0;
            Integer numAI = 0;
            Integer numELEC = 0;

            for(ChosenStudentData stu: ds.getStudents()) {
                if(stu.getSpecialism().equals("COMP")) {
                    numComp++;
                } else if (stu.getSpecialism().equals("SE")) {
                    numSE++;
                } else if (stu.getSpecialism().equals("AI")) {
                    numAI++;
                } else if (stu.getSpecialism().equals("CS")) {
                    numCS++;
                } else {
                    numELEC++;
                }
            }

            Assert.assertEquals(List.of(25,0,25,25,25),List.of(numComp,numSE,numAI,numCS,numELEC));
        }

        @Test
        public void testStuUniqueEmails() {
            DataSetFactory dsFactory = new DataSetFactory();
            DataSet ds = dsFactory.genDataSet(100, 50, 80, 10, 10, 0, 0, 25, 25, 25, 25, 0, 25, 25, 25, 25, 0, 1, 2, 50, 20, 3, 5);
            Set<String> emails = new HashSet<>();

            for(ChosenStudentData stu: ds.getStudents()) {
                emails.add(stu.getUsername());
            }

            Assert.assertEquals(100,emails.size());
        }

        @Test
        public void testSupSpecialismDist() {
            DataSetFactory dsFactory = new DataSetFactory();
            DataSet ds = dsFactory.genDataSet(100, 100, 80, 10, 10, 0, 0, 25, 25, 25, 25, 0, 25, 25, 25, 25, 0, 1, 2, 50, 20, 3, 5);
            Integer numComp = 0;
            Integer numSE = 0;
            Integer numCS = 0;
            Integer numAI = 0;
            Integer numELEC = 0;

            for(ChosenSupervisorData sup: ds.getSupervisors()) {
                if(sup.getSpecialism().equals("COMP")) {
                    numComp++;
                } else if (sup.getSpecialism().equals("SE")) {
                    numSE++;
                } else if (sup.getSpecialism().equals("AI")) {
                    numAI++;
                } else if (sup.getSpecialism().equals("CS")) {
                    numCS++;
                } else {
                    numELEC++;
                }
            }

            Assert.assertEquals(List.of(25,0,25,25,25),List.of(numComp,numSE,numAI,numCS,numELEC));
        }


        @Test
        public void testSupUniqueEmails() {
            DataSetFactory dsFactory = new DataSetFactory();
            DataSet ds = dsFactory.genDataSet(100, 50, 80, 10, 10, 0, 0, 25, 25, 25, 25, 0, 100, 0, 0, 0, 0, 1, 2, 50, 20, 3, 5);
            Set<String> emails = new HashSet<>();

            for(ChosenSupervisorData sup: ds.getSupervisors()) {
                emails.add(sup.getUsername());
            }

            Assert.assertEquals(50,emails.size());
        }

        @Test
        public void testGenDataSetSpecifyTree() {
            DataSetBuilder builder = new DataSetBuilder()
                    .numStudents(100)
                    .numSupervisors(200)
                    .perc1st(100)
                    .perc21(0)
                    .perc22(0)
                    .perc3rd(0)
                    .percLT3rd(0)
                    .stuPercComp(100)
                    .stuPercAi(0)
                    .stuPercElec(0)
                    .stuPercCS(0)
                    .stuPercSE(0)
                    .supPercComp(100)
                    .supPercAi(0)
                    .supPercElec(0)
                    .supPercCS(0)
                    .supPercSE(0)
                    .minSupervisees(1)
                    .maxSupervisees(2)
                    .numInterests(20)
                    .numSelectedInterests(5)
                    .minChildren(1)
                    .maxChildren(3);

            Node tree = new Node(0);
            DataSetFactory dsFactory = new DataSetFactory();
            DataSet ds = dsFactory.genDataSetSpecifyTree(builder,tree);
            Node dsNode = ds.getSuperInterestTree();

            Assert.assertTrue(tree.equals(dsNode));
        }
    }
}
