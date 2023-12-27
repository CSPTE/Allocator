package ac.uk.soton.ecs.projectalloc.allocator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import ac.uk.soton.ecs.projectalloc.*;

import java.util.List;
import java.util.Map;

import ac.uk.soton.ecs.projectalloc.datagen.TreeFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class AllocatorStableMarriageTest {
    static Node rootTree = new Node(0);
    static Node compTree = new Node(1, "COMP");
    static Node elecTree = new Node(2, "ELEC");
    static Node aiTree = new Node(3, "COMP_AI");
    static Node csTree = new Node(4, "COMP_CS");
    static Node ml = new Node(5, "ML");
    static Node nlp = new Node(6, "NLP");
    static Node cs1 = new Node(7, "CS1");

    static Node student1Tree = new Node(3, "COMP_AI");
    static Node student2Tree = new Node(1, "COMP");
    static Node student3Tree = new Node(4, "COMP_CS");
    static Node student4Tree = new Node(3, "COMP_AI");
    static Node student5Tree = new Node(2, "ELEC");

    static Node supervisor1Tree = new Node(1, "COMP");
    static Node supervisor2Tree = new Node(3, "COMP_AI");
    static Node supervisor3Tree = new Node(3, "COMP_AI");
    static Node supervisor4Tree = new Node(3, "COMP_AI");
    static Node supervisor5Tree = new Node(2, "ELEC");

    public ChosenStudentData createStudentWithInterestTree(String userName, int grade, Node tree) {
        ChosenStudentData studentData = new ChosenStudentData(new Student(userName), grade);
        studentData.setInterestTree(tree);

        return studentData;
    }

    public ChosenSupervisorData createSupervisorWithInterestTree(String userName, int supervisees, Node tree) {
        ChosenSupervisorData supervisorData = new ChosenSupervisorData(new Supervisor(userName), supervisees);
        supervisorData.setInterestTree(tree);

        return supervisorData;
    }

    @BeforeAll
    static void setUpTrees() {
        rootTree.addChild(compTree);
        rootTree.addChild(elecTree);
        compTree.addChild(aiTree);
        compTree.addChild(csTree);
        aiTree.addChild(ml);
        aiTree.addChild(nlp);
        csTree.addChild(cs1);

        student1Tree.addChildren(List.of(new Node(5, "ML"), new Node(6, "NLP")));
        student3Tree.addChild(new Node(7, "CS1"));
        student4Tree.addChildren(List.of(new Node(5, "ML"), new Node(6, "NLP")));

        supervisor1Tree.addChildren(List.of(new Node(3, "COMP_AI"), new Node(4, "COMP_CS")));
        supervisor1Tree.getChildren().get(0).addChildren(List.of(new Node(5, "ML"), new Node(6, "NLP")));
        supervisor1Tree.getChildren().get(1).addChild(new Node(7, "CS1"));
        supervisor3Tree.addChild(new Node(5, "ML"));
        supervisor4Tree.addChildren(List.of(new Node(5, "ML"), new Node(6, "NLP")));
    }

    @Test
    void testPreferencesOrderingForStudent() {
        ChosenStudentData student1 = new ChosenStudentData(new Student("0001"), 50);
        student1.setInterestTree(student1Tree);
        ChosenStudentData student2 = new ChosenStudentData(new Student("0002"), 60);
        student2.setInterestTree(student2Tree);
        ChosenStudentData student3 = new ChosenStudentData(new Student("0003"), 70);
        student3.setInterestTree(student3Tree);
        ChosenStudentData student4 = new ChosenStudentData(new Student("0004"), 80);
        student4.setInterestTree(student4Tree);
        ChosenStudentData student5 = new ChosenStudentData(new Student("0005"), 90);
        student5.setInterestTree(student5Tree);
        List<ChosenStudentData> students = List.of(student1, student2, student3, student4, student5);

        ChosenSupervisorData supervisor1 = new ChosenSupervisorData(new Supervisor("1000"), 5);
        supervisor1.setInterestTree(supervisor1Tree);
        ChosenSupervisorData supervisor2 = new ChosenSupervisorData(new Supervisor("2000"), 4);
        supervisor2.setInterestTree(supervisor2Tree);
        ChosenSupervisorData supervisor3 = new ChosenSupervisorData(new Supervisor("3000"), 3);
        supervisor3.setInterestTree(supervisor3Tree);
        ChosenSupervisorData supervisor4 = new ChosenSupervisorData(new Supervisor("4000"), 2);
        supervisor4.setInterestTree(supervisor4Tree);
        ChosenSupervisorData supervisor5 = new ChosenSupervisorData(new Supervisor("5000"), 1);
        supervisor5.setInterestTree(supervisor5Tree);
        List<ChosenSupervisorData> supervisors = List.of(supervisor1, supervisor2, supervisor3, supervisor4, supervisor5);

        AllocatorStableMarriageAlgorithm sm = new AllocatorStableMarriageAlgorithm(students, supervisors);
        sm.createPreferenceLists();

        Map<ChosenStudentData, List<ChosenSupervisorData>> studentPreferenceList = sm.getStudentPreferenceList();
        List<ChosenSupervisorData> student1Preferences = studentPreferenceList.get(student1);

        // Expected order of supervisors for student1: "1", "4", "3", "2", "5"
        assertEquals("1000", student1Preferences.get(0).getUsername());
        assertEquals("4000", student1Preferences.get(1).getUsername());
        assertEquals("3000", student1Preferences.get(2).getUsername());
        assertEquals("2000", student1Preferences.get(3).getUsername());
        assertEquals("5000", student1Preferences.get(4).getUsername());
    }

    @Test
    void testPreferencesOrderingForSupervisor() {
        ChosenStudentData student1 = createStudentWithInterestTree("0001", 50, student1Tree);
        ChosenStudentData student2 = createStudentWithInterestTree("0002", 60, student2Tree);
        ChosenStudentData student3 = createStudentWithInterestTree("0003", 70, student3Tree);
        ChosenStudentData student4 = createStudentWithInterestTree("0004", 80, student4Tree);
        ChosenStudentData student5 = createStudentWithInterestTree("0005", 90, student5Tree);
        List<ChosenStudentData> students = List.of(student1, student2, student3, student4, student5);

        ChosenSupervisorData supervisor1 = createSupervisorWithInterestTree("1000", 5, supervisor1Tree);
        ChosenSupervisorData supervisor2 = createSupervisorWithInterestTree("2000", 4, supervisor2Tree);
        ChosenSupervisorData supervisor3 = createSupervisorWithInterestTree("3000", 3, supervisor3Tree);
        ChosenSupervisorData supervisor4 = createSupervisorWithInterestTree("4000", 2, supervisor4Tree);
        ChosenSupervisorData supervisor5 = createSupervisorWithInterestTree("5000", 1, supervisor5Tree);
        List<ChosenSupervisorData> supervisors = List.of(supervisor1, supervisor2, supervisor3, supervisor4, supervisor5);

        AllocatorStableMarriageAlgorithm sm = new AllocatorStableMarriageAlgorithm(students, supervisors);
        sm.createPreferenceLists();

        Map<ChosenSupervisorData, List<ChosenStudentData>> supervisorPreferenceList = sm.getSupervisorPreferenceList();
        List<ChosenStudentData> supervisor1Preferences = supervisorPreferenceList.get(supervisor1);

        // Expected order of students for supervisor1: "4", "1", "3", "2", "5"
        assertEquals("0004", supervisor1Preferences.get(0).getUsername());
        assertEquals("0003", supervisor1Preferences.get(1).getUsername());
        assertEquals("0002", supervisor1Preferences.get(2).getUsername());
        assertEquals("0001", supervisor1Preferences.get(3).getUsername());
        assertEquals("0005", supervisor1Preferences.get(4).getUsername());
    }

    @Test
    void testStableMarriageAlgorithm() throws NoViableSolutionException {
        TreeFactory treeFactory = new TreeFactory();
        Node superTree = treeFactory.generateTree(12, 3, 3);
        Node subTree1 = superTree.getChildren().get(0);
        Node subTree2 = superTree.getChildren().get(1);
        Node subTree3 = superTree.getChildren().get(2);

        ChosenStudentData student1 = createStudentWithInterestTree("0001", 50, subTree1);
        ChosenStudentData student2 = createStudentWithInterestTree("0002", 60, subTree2);
        ChosenStudentData student3 = createStudentWithInterestTree("0003", 70, subTree3);
        List<ChosenStudentData> students = List.of(student1, student2, student3);

        ChosenSupervisorData supervisor1 = createSupervisorWithInterestTree("1000", 2, subTree1);
        ChosenSupervisorData supervisor2 = createSupervisorWithInterestTree("2000", 1, subTree3);
        ChosenSupervisorData supervisor3 = createSupervisorWithInterestTree("3000", 1, subTree2);
        List<ChosenSupervisorData> supervisors = List.of(supervisor1, supervisor2, supervisor3);

        AllocatorStableMarriageAlgorithm sm = new AllocatorStableMarriageAlgorithm(students, supervisors);
        List<Pairing> pairings = sm.generatePairings();

        assertEquals(3, pairings.size());

        assertEquals("0001", pairings.get(0).getStudent().getUsername());
        assertEquals("1000", pairings.get(0).getSupervisor().getUsername());

        assertEquals("0002", pairings.get(1).getStudent().getUsername());
        assertEquals("3000", pairings.get(1).getSupervisor().getUsername());

        assertEquals("0003", pairings.get(2).getStudent().getUsername());
        assertEquals("2000", pairings.get(2).getSupervisor().getUsername());
    }

    @Test
    void testTutor() throws NoViableSolutionException {
        ChosenStudentData student1 = createStudentWithInterestTree("0001", 50, student1Tree);
        ChosenStudentData student2 = createStudentWithInterestTree("0002", 60, student2Tree);
        ChosenStudentData student3 = createStudentWithInterestTree("0003", 70, student3Tree);
        List<ChosenStudentData> students = List.of(student1, student2, student3);

        ChosenSupervisorData supervisor1 = createSupervisorWithInterestTree("1000", 5, supervisor1Tree);
        ChosenSupervisorData supervisor2 = createSupervisorWithInterestTree("2000", 4, supervisor2Tree);
        ChosenSupervisorData supervisor3 = createSupervisorWithInterestTree("3000", 3, supervisor3Tree);
        List<ChosenSupervisorData> supervisors = List.of(supervisor1, supervisor2, supervisor3);

        student1.setTutor(supervisor1.getSupervisor());
        student2.setTutor(supervisor2.getSupervisor());
        student3.setTutor(supervisor3.getSupervisor());

        AllocatorStableMarriageAlgorithm sm = new AllocatorStableMarriageAlgorithm(students, supervisors);
        List<Pairing> pairings = sm.generatePairings();

        assertEquals(3, pairings.size());

        assertNotEquals(pairings.get(0).getSupervisor().getUsername(), pairings.get(0).getStudent().getUsername());
        assertNotEquals(pairings.get(1).getSupervisor().getUsername(), pairings.get(1).getStudent().getUsername());
        assertNotEquals(pairings.get(2).getSupervisor().getUsername(), pairings.get(2).getStudent().getUsername());
    }
}

