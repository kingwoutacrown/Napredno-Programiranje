import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

// TODO: Add classes and implement methods
class SubjectWithGrade
{
    private String subject;
    private int grade;

    public SubjectWithGrade(String subject, int grade) {
        this.subject = subject;
        this.grade = grade;
    }

    public String getSubject() {
        return subject;
    }
    public int getGrade() {
        return grade;
    }
    public void setSubject(String subject) { this.subject = subject; }
    public void setGrade(int grade) { this.grade = grade; }
}

class Applicant {
    private int id;
    private String name;
    private double gpa;
    private List<SubjectWithGrade> subjectsWithGrade;
    private StudyProgramme studyProgramme;
    private double points;

    public Applicant(int id, String name, double gpa, StudyProgramme studyProgramme) {
        this.id = id;
        this.name = name;
        this.gpa = gpa;
        this.subjectsWithGrade = new ArrayList<>();
        this.studyProgramme = studyProgramme;
        this.points = 0.0;
    }

    public void addSubjectAndGrade(String subject, int grade) {
        this.subjectsWithGrade.add(new SubjectWithGrade(subject, grade));
    }

    public void calculatePoints() {

        double total = this.gpa * 12;

        List<String> appropriateSubjects = this.studyProgramme.getFaculty().getAppropriateSubjects();

        for (SubjectWithGrade subjects : subjectsWithGrade) {
            String subject =subjects.getSubject();
            int grade = subjects.getGrade();
            if (appropriateSubjects.contains(subject)) {
                total += (grade * 2);
            } else {
                total += (grade * 1.2);
            }
        }
        this.points = total;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getGpa() {
        return gpa;
    }

    public List<SubjectWithGrade> getSubjectsWithGrade() {
        return subjectsWithGrade;
    }

    public StudyProgramme getStudyProgramme() {
        return studyProgramme;
    }

    public double getPoints() {
        return points;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setGpa(double gpa) {
        this.gpa = gpa;
    }
    public void setStudyProgramme(StudyProgramme studyProgramme) {
        this.studyProgramme = studyProgramme;
    }

    @Override
    public String toString() {
        double floatFix = Math.round(this.points * 10.0) / 10.0;
        String pointsFix;
        if(floatFix != this.points) {
            pointsFix = String.format("%.16G", this.points);
        }
        else {
            pointsFix = String.format("%.1f", this.points);
        }
        return String.format("Id: %d, Name: %s, GPA: %.1f - %s", id, name, gpa, pointsFix);
    }
}

class StudyProgramme {
    private String code;
    private String name;
    private Faculty faculty;
    private int numPublicQuota;
    private int numPrivateQuota;
    private int enrolledInPublicQuota;
    private int enrolledInPrivateQuota;
    private List<Applicant> applicants;

    public StudyProgramme(String code, String name, Faculty faculty, int numPublicQuota, int numPrivateQuota) {
        this.code = code;
        this.name = name;
        this.faculty = faculty;
        this.numPublicQuota = numPublicQuota;
        this.numPrivateQuota = numPrivateQuota;
        this.enrolledInPublicQuota = 0;
        this.enrolledInPrivateQuota = 0;
        this.applicants = new ArrayList<>();

    }

    public void addApplicant(Applicant applicant) {
        this.applicants.add(applicant);
    }

    public void calculateEnrollmentNumbers() {
        applicants.forEach(Applicant::calculatePoints);
        applicants.sort(Comparator.comparing(Applicant::getPoints).reversed());
        this.enrolledInPublicQuota = 0;
        this.enrolledInPrivateQuota = 0;

        for (Applicant applicant : applicants) {
            if (enrolledInPublicQuota < numPublicQuota) {
                enrolledInPublicQuota++;
            } else if (enrolledInPrivateQuota < numPrivateQuota) {
                enrolledInPrivateQuota++;
            } else {
                break;
            }
        }
    }
    public double getEnrollmentPercentage() {
        int totalQuota = numPublicQuota + numPrivateQuota;
        int enrolled = enrolledInPublicQuota + enrolledInPrivateQuota;
        if (totalQuota == 0) return 0.0;
        return (double) enrolled / totalQuota * 100.0;
    }
    public String getCode() {
        return code;
    }
    public String getName() {
        return name;
    }
    public Faculty getFaculty() {
        return faculty;
    }
    public int getNumPublicQuota() {
        return numPublicQuota;
    }
    public int getNumPrivateQuota() {
        return numPrivateQuota;
    }
    public int getEnrolledInPublicQuota() {
        return enrolledInPublicQuota;
    }
    public int getEnrolledInPrivateQuota() {
        return enrolledInPrivateQuota;
    }
    public List<Applicant> getApplicants() {
        return applicants;
    }

    public void setCode(String code) {
        this.code = code;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }
    public void setNumPublicQuota(int numPublicQuota) {
        this.numPublicQuota = numPublicQuota;
    }
    public void setNumPrivateQuota(int numPrivateQuota) {
        this.numPrivateQuota = numPrivateQuota;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Name: %s\n", name));

        int totalEnrolled = enrolledInPublicQuota + enrolledInPrivateQuota;

        List<Applicant> accepted = applicants.stream()
                .limit(totalEnrolled)
                .collect(Collectors.toList());

        List<Applicant> rejected = applicants.stream()
                .skip(totalEnrolled)
                .collect(Collectors.toList());
        sb.append("Public Quota: \n");
        accepted.stream()
                .limit(enrolledInPublicQuota)
                .forEach(a -> sb.append(a.toString()).append("\n"));
        sb.append("Private Quota: \n");
        accepted.stream()
                .skip(enrolledInPublicQuota)
                .limit(enrolledInPrivateQuota)
                .forEach(a -> sb.append(a.toString()).append("\n"));
        sb.append("Rejected: \n");
        rejected.forEach(a -> sb.append(a.toString()).append("\n"));

        return sb.toString();
    }
}

class Faculty {
    private String shortName;
    private List<String> appropriateSubjects;
    private List<StudyProgramme> studyProgrammes;

    public Faculty(String shortName) {
        this.shortName = shortName;
        this.appropriateSubjects = new ArrayList<>();
        this.studyProgrammes = new ArrayList<>();
    }
    public void addSubject(String subject) {
        this.appropriateSubjects.add(subject);
    }
    public void addStudyProgramme(StudyProgramme sp) {
        this.studyProgrammes.add(sp);
    }

    public String getShortName() {
        return shortName;
    }
    public List<String> getAppropriateSubjects() {
        return appropriateSubjects;
    }
    public List<StudyProgramme> getStudyProgrammes() {
        return studyProgrammes;
    }
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
    public void setAppropriateSubjects(List<String> appropriateSubjects) {
        this.appropriateSubjects = appropriateSubjects;
    }
    public void setStudyProgrammes(List<StudyProgramme> studyProgrammes) {
        this.studyProgrammes = studyProgrammes;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Faculty: %s\n", shortName));
        sb.append(String.format("Subjects: %s\n", appropriateSubjects));
        sb.append("Study Programmes: \n");
        Comparator<StudyProgramme> studyProgrammeComparator = Comparator
                .comparing((StudyProgramme sp) -> sp.getFaculty().getAppropriateSubjects().size())
                .thenComparing(StudyProgramme::getEnrollmentPercentage, Comparator.reverseOrder());
        List<StudyProgramme> sortedProgrammes = studyProgrammes.stream()
                .sorted(studyProgrammeComparator)
                .collect(Collectors.toList());
        for (StudyProgramme sp : sortedProgrammes) {
            sb.append(sp.toString()).append("\n");
        }
        return sb.toString();
    }
}

class EnrollmentsIO {

    public static void readEnrollments(List<StudyProgramme> studyProgrammes, InputStream inputStream) {
        Map<String, StudyProgramme> programmeMap = studyProgrammes.stream()
                .collect(Collectors.toMap(StudyProgramme::getCode, Function.identity()));

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(";");

                try {
                    int id = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    double gpa = Double.parseDouble(parts[2]);
                    String studyProgrammeCode = parts[parts.length - 1];
                    StudyProgramme sp = programmeMap.get(studyProgrammeCode);
                    if (sp == null) {
                        continue;
                    }
                    Applicant applicant = new Applicant(id, name, gpa, sp);
                    for (int i = 3; i < parts.length - 1; i += 2) {
                        if (i + 1 < parts.length - 1) {
                            String subject = parts[i];
                            try {
                                int grade = Integer.parseInt(parts[i + 1]);
                                applicant.addSubjectAndGrade(subject, grade);
                            } catch (NumberFormatException e) {
                                continue;
                            }
                        }
                    }
                    sp.addApplicant(applicant);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    continue;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading input: " + e.getMessage());
        }
    }

    public static void printRanked(List<Faculty> faculties) {
        for (Faculty faculty : faculties) {
            System.out.println(faculty.toString());
        }
    }
}

public class EnrollmentsTest {

    public static void main(String[] args) {
        Faculty finki = new Faculty("FINKI");
        finki.addSubject("Mother Tongue");
        finki.addSubject("Mathematics");
        finki.addSubject("Informatics");

        Faculty feit = new Faculty("FEIT");
        feit.addSubject("Mother Tongue");
        feit.addSubject("Mathematics");
        feit.addSubject("Physics");
        feit.addSubject("Electronics");

        Faculty medFak = new Faculty("MEDFAK");
        medFak.addSubject("Mother Tongue");
        medFak.addSubject("English");
        medFak.addSubject("Mathematics");
        medFak.addSubject("Biology");
        medFak.addSubject("Chemistry");

        StudyProgramme si = new StudyProgramme("SI", "Software Engineering", finki, 4, 4);
        StudyProgramme it = new StudyProgramme("IT", "Information Technology", finki, 2, 2);
        finki.addStudyProgramme(si);
        finki.addStudyProgramme(it);

        StudyProgramme kti = new StudyProgramme("KTI", "Computer Technologies and Engineering", feit, 3, 3);
        StudyProgramme ees = new StudyProgramme("EES", "Electro-energetic Systems", feit, 2, 2);
        feit.addStudyProgramme(kti);
        feit.addStudyProgramme(ees);

        StudyProgramme om = new StudyProgramme("OM", "General Medicine", medFak, 6, 6);
        StudyProgramme nurs = new StudyProgramme("NURS", "Nursing", medFak, 2, 2);
        medFak.addStudyProgramme(om);
        medFak.addStudyProgramme(nurs);

        List<StudyProgramme> allProgrammes = new ArrayList<>();
        allProgrammes.add(si);
        allProgrammes.add(it);
        allProgrammes.add(kti);
        allProgrammes.add(ees);
        allProgrammes.add(om);
        allProgrammes.add(nurs);

        EnrollmentsIO.readEnrollments(allProgrammes, System.in);

        List<Faculty> allFaculties = new ArrayList<>();
        allFaculties.add(finki);
        allFaculties.add(feit);
        allFaculties.add(medFak);

        allProgrammes.stream().forEach(StudyProgramme::calculateEnrollmentNumbers);

        EnrollmentsIO.printRanked(allFaculties);

    }


}
