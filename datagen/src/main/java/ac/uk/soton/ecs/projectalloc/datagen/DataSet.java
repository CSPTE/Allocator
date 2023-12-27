package ac.uk.soton.ecs.projectalloc.datagen;

import ac.uk.soton.ecs.projectalloc.ChosenStudentData;
import ac.uk.soton.ecs.projectalloc.ChosenSupervisorData;
import ac.uk.soton.ecs.projectalloc.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class DataSet {

    public Node superInterestTree;
    public final ArrayList<ChosenStudentData> students = new ArrayList<>();
    public final ArrayList<ChosenSupervisorData> supervisors = new ArrayList<>();

    public void addStudent(ChosenStudentData student) {students.add(student);}

    public void addStudents(List<ChosenStudentData> students) {this.students.addAll(students);}

    public void removeStudent(ChosenStudentData student) {students.remove(student);}

    public void removeStudents(List<ChosenStudentData> students) {this.students.removeAll(students);}

    public boolean containsStudent(ChosenStudentData student) {return students.contains(student);}

    public List<ChosenStudentData> getStudents() {return Collections.unmodifiableList(students);}

    public void addSupervisor(ChosenSupervisorData supervisor) {supervisors.add(supervisor);}

    public void addSupervisors(List<ChosenSupervisorData> supervisors) {this.supervisors.addAll(supervisors);}

    public void removeSupervisor(ChosenSupervisorData supervisor) {supervisors.remove(supervisor);}

    public void removeSupervisors(List<ChosenSupervisorData> supervisors) {this.supervisors.removeAll(supervisors);}

    public boolean containsSupervisor(ChosenSupervisorData supervisor) {return supervisors.contains(supervisor);}

    public List<ChosenSupervisorData> getSupervisors() {return Collections.unmodifiableList(supervisors);}

    public void setSuperInterestTree(Node tree) {this.superInterestTree = tree;}

    public Node getSuperInterestTree() {return this.superInterestTree;}
}
