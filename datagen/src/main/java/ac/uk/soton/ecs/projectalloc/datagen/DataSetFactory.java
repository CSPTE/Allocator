package ac.uk.soton.ecs.projectalloc.datagen;

import ac.uk.soton.ecs.projectalloc.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataSetFactory {
    public DataSet genDataSet(Integer numStudents, Integer numSupervisors,Integer perc1st,
        Integer perc21,Integer perc22, Integer perc3rd, Integer percLT3rd, Integer stuPercComp,
        Integer stuPercAi, Integer stuPercElec, Integer stuPercCS, Integer stuPercSE,
        Integer supPercComp, Integer supPercAi, Integer supPercElec, Integer supPercCS,
        Integer supPercSE, Integer minSupervisees, Integer maxSupervisees, Integer numInterests,
        Integer numSelectedInterests, Integer minChildren, Integer maxChildren) {
        return new DataSetBuilder()
            .numStudents(numStudents)
            .numSupervisors(numSupervisors)
            .perc1st(perc1st)
            .perc21(perc21)
            .perc22(perc22)
            .perc3rd(perc3rd)
            .percLT3rd(percLT3rd)
            .stuPercComp(stuPercComp)
            .stuPercAi(stuPercAi)
            .stuPercElec(stuPercElec)
            .stuPercCS(stuPercCS)
            .stuPercSE(stuPercSE)
            .supPercComp(supPercComp)
            .supPercAi(supPercAi)
            .supPercElec(supPercElec)
            .supPercCS(supPercCS)
            .supPercSE(supPercSE)
            .minSupervisees(minSupervisees)
            .maxSupervisees(maxSupervisees)
            .numInterests(numInterests)
            .numSelectedInterests(numSelectedInterests)
            .minChildren(minChildren)
            .maxChildren(maxChildren)
            .build();
    }

    public void validateBuilder(DataSetBuilder builder){
        for(Field field : builder.getClass().getDeclaredFields()) {
            try {
                if(field.getInt(builder) == Integer.MIN_VALUE) {
                    throw new IllegalArgumentException("Missing params: " + field.getName());
                }
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Invalid params: " + field.getName());
            }
        }

        if(builder.numStudents <= 0) {
            throw new IllegalArgumentException("Must generate at least 1 student");
        }else if (builder.numSupervisors <= 0) {
            throw new IllegalArgumentException("Must generate at least 1 supervisor");
        }else if (builder.perc1st < 0 | builder.perc21 < 0 | builder.perc22 < 0 | builder.perc3rd < 0 | builder.percLT3rd < 0) {
            throw new IllegalArgumentException("All student grade distribution percentages must be greater than or equal to 0");
        }else if (builder.stuPercAi < 0 | builder.stuPercComp < 0 | builder.stuPercCS < 0 | builder.stuPercSE < 0 | builder.stuPercElec < 0) {
            throw new IllegalArgumentException("All student specialism distribution percentages must be greater than equal to 0");
        } else if (builder.supPercAi < 0 | builder.supPercComp < 0 | builder.supPercCS < 0 | builder.supPercSE < 0 | builder.supPercElec < 0) {
            throw new IllegalArgumentException("All supervisor specialism distribution percentages must be greater than equal to 0");
        }else if(builder.perc1st + builder.perc21 + builder.perc22 + builder.perc3rd + builder.percLT3rd != 100) {
            throw new IllegalArgumentException("Percentage distribution of grades should sum to 100");
        } else if(builder.stuPercAi + builder.stuPercComp + builder.stuPercCS + builder.stuPercElec + builder.stuPercSE != 100) {
            throw  new IllegalArgumentException("Percentage distribution of student's specialisms should sum to 100");
        } else if(builder.supPercAi + builder.supPercComp + builder.supPercCS + builder.supPercElec + builder.supPercSE != 100) {
            throw new IllegalArgumentException("Percentage distribution of supervisor's specialisms should sum to 100");
        } else if (builder.minSupervisees < 1 | builder.maxSupervisees < 1) {
            throw new IllegalArgumentException("Min and max supervisees must be greater than or equal to 1");
        } else if (builder.minSupervisees > builder.maxSupervisees) {
            throw new IllegalArgumentException("Min supervisees should be less than max supervisees");
        } else if (builder.minChildren > builder.maxChildren) {
            throw new IllegalArgumentException("Min children should be less than max children");
        } else if (builder.minChildren < 1 | builder.maxChildren < 1) {
            throw new IllegalArgumentException("Min and max children must be greater than or equal to 1");
        } else if (builder.numStudents > builder.numSupervisors * builder.maxSupervisees) {
            throw new IllegalArgumentException("Less available allocations than students.");
        }
    }

    public DataSet genDataSetSpecifyTree(DataSetBuilder builder, Node superTree) {
        //Input validation
        validateBuilder(builder);

        TreeFactory tf = new TreeFactory();
        DataSet ds = new DataSet();
        List<Supervisor> supervisors = new ArrayList<>();
        Map<String,Integer> supSpecPercMap = new HashMap<>();
        Map<String,Integer> stuGradePercMap = new HashMap<>();
        Integer specPerc;
        Integer numStuCreated = 0;
        Integer numSupCreated = 0;

        ds.setSuperInterestTree(superTree);

        stuGradePercMap.put("1st",builder.perc1st);
        stuGradePercMap.put("21",builder.perc21);
        stuGradePercMap.put("22",builder.perc22);
        stuGradePercMap.put("3rd",builder.perc3rd);
        stuGradePercMap.put("LT3rd",builder.percLT3rd);

        supSpecPercMap.put("COMP", builder.supPercComp);
        supSpecPercMap.put("ELEC", builder.supPercElec);
        supSpecPercMap.put("AI", builder.supPercAi);
        supSpecPercMap.put("CS", builder.supPercCS);
        supSpecPercMap.put("SE", builder.supPercSE);

        for(String supSpec: supSpecPercMap.keySet()) {
            specPerc = supSpecPercMap.get(supSpec);
            Double numSupToCreate = (Double.valueOf(specPerc)/100.0) * builder.numSupervisors;

            for(int i = 0; i < numSupToCreate; i++) { //Create supervisors with specialism
                if(ds.getSupervisors().size() == builder.numSupervisors) {
                    break;
                }

                Supervisor sup = new Supervisor("sup" + numSupCreated + "@soton.ac.uk");
                Integer maxAllocations = (int) (Math.random() * (builder.maxSupervisees - builder.minSupervisees) + builder.minSupervisees);
                Node tree = tf.getSubTree(superTree, builder.numSelectedInterests);
                ChosenSupervisorData chosenSup = new ChosenSupervisorData(sup,maxAllocations);
                numSupCreated++;
                chosenSup.setSpecialism(supSpec);
                chosenSup.setInterestTree(tree);
                ds.addSupervisor(chosenSup);
                supervisors.add(sup);
            }
        }

        for(String grade: stuGradePercMap.keySet()) {

            Integer gradePerc = stuGradePercMap.get(grade);
            String specialism = null;
            double numCOMP = (double) builder.stuPercComp / 100 * Double.valueOf(gradePerc)/100 * builder.numStudents;
            double numAI = (double) builder.stuPercAi / 100 * Double.valueOf(gradePerc)/100 * builder.numStudents;
            double numSE = (double) builder.stuPercSE / 100 * Double.valueOf(gradePerc)/100 * builder.numStudents;
            double numCS = (double) builder.stuPercCS / 100 * Double.valueOf(gradePerc)/100 * builder.numStudents;
            double numELEC = (double) builder.stuPercElec / 100 * Double.valueOf(gradePerc)/100 * builder.numStudents;
            double numStuToCreate = Double.valueOf(gradePerc)/100 * builder.numStudents;



            for(int i = 0; i < numStuToCreate; i++) {
                if(ds.getStudents().size() == builder.numStudents) {
                    break;
                }

                if(numCOMP > 0) {
                    specialism = "COMP";
                    numCOMP--;
                } else if(numAI > 0) {
                    specialism = "AI";
                    numAI--;
                } else if(numSE > 0) {
                    specialism = "SE";
                    numSE--;
                } else if(numCS > 0) {
                    specialism = "CS";
                    numCS--;
                } else if(numELEC > 0) {
                    specialism = "ELEC";
                    numELEC--;
                }

                Student stu = new Student("stu" + numStuCreated + "@soton.ac.uk");
                Integer tutorIndex = (int) Math.round(Math.random() * (builder.numSupervisors-1));
                Node tree = tf.getSubTree(superTree,builder.numSelectedInterests);
                Supervisor tutor = supervisors.get(tutorIndex);
                ChosenStudentData chosenStu = new ChosenStudentData(stu, genGrade(grade));
                chosenStu.setSpecialism(specialism);
                chosenStu.setTutor(tutor);
                chosenStu.setInterestTree(tree);
                ds.addStudent(chosenStu);
                numStuCreated++;
            }
        }

        return ds;
    }

    public Integer genGrade(String grade) {
        return switch (grade) {
            case "1st" -> (int) (Math.random() * 30) + 70;
            case "21" -> (int) (Math.random() * 10) + 60;
            case "22" -> (int) (Math.random() * 10) + 50;
            case "3rd" -> (int) (Math.random() * 10) + 40;
            case "LT3rd" -> (int) (Math.random() * 40);
            default -> throw new IllegalArgumentException("Grade string feed is not a grade.");
        };
    }

    public DataSet genDataSet(DataSetBuilder builder) {
        validateBuilder(builder);
        TreeFactory tf = new TreeFactory();
        Node superTree = tf.generateTree(builder.numInterests, builder.minChildren, builder.maxChildren);
        return genDataSetSpecifyTree(builder,superTree);
    }

    public void exportDataSet(DataSet dataSet, String filePath,String fileName) {
        String dir = filePath + File.separator + fileName + ".json";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(new File(dir),dataSet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String dataSetToJSONString(DataSet dataSet) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(dataSet);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
