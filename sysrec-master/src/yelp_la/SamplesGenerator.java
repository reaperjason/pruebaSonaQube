package test1;

import java.io.File;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;
import java.io.FileWriter;

class SamplesGenerator {

    String pathGroupsUsers = "data/yelp_la/groupid_users_clean.dat";
    String pathSampleGroupsUsers = "data/yelp_la/sample_groupid_users.dat";

    String pathUsersEvents = "data/yelp_la/user_events.dat";
    String pathSampleUsersEvents = "data/yelp_la/sample_user_events.dat";

    String pathGroupsEvents = "data/yelp_la/groupid_events.dat";
    String pathSampleGroupsEvents = "data/yelp_la/sample_groupid_events.dat";

    String pathFinalSampleMatrix = "data/yelp_la/user_events_registry.dat";

    List<Integer> nGroups = new ArrayList<Integer>();
    List<Integer> nUsers = new ArrayList<Integer>();

    List<Integer> groupsId = new ArrayList<Integer>();
    List<String> usersId = new ArrayList<String>();
    List<String> usersEvents = new ArrayList<String>();

    public SamplesGenerator(int[][] ngroupsnusers) {

        for (int i = 0; i < ngroupsnusers.length; i++) {
            nGroups.add(ngroupsnusers[i][0]);
            nUsers.add(ngroupsnusers[i][1]);
        }

        addgroupId();
        System.out.println("Writting sample_groupid_users.dat...");
        writeSample(pathGroupsUsers, pathSampleGroupsUsers, groupsId);
        // groupid_users_clean, file objective, groupsId
        System.out.println("File sample_groupid_users.dat completed");

        System.out.println("Writting groupid_events.dat...");
        writeSample(pathGroupsEvents, pathSampleGroupsEvents, groupsId);
        System.out.println("File groupid_events.dat completed");
        fillUserEvents();
        System.out.println("Writting sample_user_events.dat...");
        writeSampleUsersEvents();
        System.out.println("File sample_user_events.dat completed");

        System.out.println("Writting user_events_registry.dat...");
        writeFinalMatrix();
        System.out.println("File user_events_registry.dat completed");
    
    }
    public void writeFinalMatrix(){
        File file = new File(pathFinalSampleMatrix);
        FileWriter fr = null;
        List<Integer> repeated = new ArrayList<Integer>();
        for (int i = 0; i<usersEvents.size(); i++){
            String[] aux  = usersEvents.get(i).split("\t");
            String[] events = aux[1].replaceAll("[\\t ]", "").split(",");
            if(events.length>1){
                System.out.println("Entro con índice"+i);
                for(int j = 0; j<events.length; j++){
                usersEvents.add(aux[0]+"\t"+events[j]);
                }
                repeated.add(i);
            }
        }
        Collections.sort(repeated, Collections.reverseOrder()); 
        if(repeated.size()>0){
            for (int i = 0; i < repeated.size(); i++) {
                usersEvents.remove(repeated.get(i).intValue());
            }
        }
        try {
            fr = new FileWriter(file, true);
            for (int j = 0; j < usersEvents.size(); j++) {
                fr.write(usersEvents.get(j)+"\t"+"1");
                fr.write("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // close resources
            try {
                fr.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public int randomNumberGenerator(int lower, int upper) { // Genera un entero random y lo retorna
        upper = upper + 1;
        int r = (int) (Math.random() * (upper - lower)) + lower;
        return r;
    }

    public void addgroupId() {
        for (int i = 0; i < nUsers.size(); i++) {
            for (int j = 0; j < nGroups.get(i); j++) {
                int groupId = 0;
                do {
                    groupId = searchGroupIdAndNUsers(nUsers.get(i));
                } while (groupId == -1);
                if (existeId2(groupId, groupsId))
                    j--;
                else
                    groupsId.add(groupId);
            }
        }
    }

    public boolean existeId2(int idG, List<Integer> idList) {
        for (int i = 0; i < idList.size(); i++) {
            if (idG == idList.get(i))
                return true;
        }
        return false;
    }

    public boolean existeId(String idG, List<String> idList) {
        for (int i = 0; i < idList.size(); i++) {
            if (idG.equals(idList.get(i)))
                return true;
        }
        return false;
    }

    public int searchGroupIdAndNUsers(int nUsers) {

        Scanner doc;
        int groupRandomId = randomNumberGenerator(0, 109537);
        try {
            doc = new Scanner(new File(pathGroupsUsers));
            while (doc.hasNextLine()) {
                String line = doc.nextLine();
                String[] columns = line.split("\t");
                if (columns[0].equals(String.valueOf(groupRandomId))) {
                    if (columns[1].split(",").length == nUsers) {
                        return Integer.parseInt(columns[0]);

                    }
                    break;
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return -1;
    }

    public String getLine(int idG, String pathAllFile) {
        Scanner doc;
        try {
            doc = new Scanner(new File(pathAllFile));
            while (doc.hasNextLine()) {
                String line = doc.nextLine();
                String[] columns = line.split("\t");
                if (columns[0].equals(String.valueOf(idG))) {
                    if (pathAllFile.equals(pathGroupsUsers)) {
                        addUsersId(columns[1].split(","));
                    }
                    return columns[0] + "\t" + columns[1];
                }

            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    public void writeSample(String pathAllFile, String pathSampleFile, List<Integer> listId) {
        // groupid_users_clean, file objective, groupsId
        File file = new File(pathSampleFile);
        FileWriter fr = null;
        try {
            fr = new FileWriter(file, true);
            for (int j = 0; j < listId.size(); j++) {
                fr.write(getLine(listId.get(j), pathAllFile)); // if getline != null
                fr.write("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // close resources
            try {
                fr.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void addUsersId(String[] arrayusersId) {
        for (int i = 0; i < arrayusersId.length; i++) {
            if (!existeId((arrayusersId[i]), usersId)) // Arrayusers tiene array de strings
                usersId.add(arrayusersId[i]);
            // System.out.println("Size usersId:"+usersId.size()+" User
            // added:"+usersId.get(i));
        }
    }

    public List<Integer> getGroupsByUserId(String userId) {
        Scanner doc;
        List<Integer> groups = new ArrayList<Integer>();
        try {
            doc = new Scanner(new File(pathSampleGroupsUsers));
            while (doc.hasNextLine()) {
                String line = doc.nextLine();
                String[] columns = line.split("\t");
                for (int i = 0; i < columns[1].split(",").length; i++) {
                    if (userId.equals((columns[1].split(",")[i]))) {
                        groups.add(Integer.parseInt(columns[0]));
                    }
                }

            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return groups;
    }

    public List<String> getEventsByListGroups(List<Integer> groups) {
        Scanner doc;
        List<String> events = new ArrayList<String>();
        try {
            doc = new Scanner(new File(pathSampleGroupsEvents));
            while (doc.hasNextLine()) {
                String line = doc.nextLine();
                String[] columns = line.split("\t");
                for (int i = 0; i < groups.size(); i++) {
                    if (groups.get(i) == Integer.parseInt(columns[0])) {
                        for (int j = 0; j < columns[1].split(",").length; j++) {
                            if (!existeId((columns[1].split(",")[j]), events)) {
                                events.add((columns[1].split(",")[j]));
                            }
                        }

                    }
                }

            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return events;
    }

    public String listToString(List<Integer> events) {
        String stringEvents = "";
        for (int i = 0; i < events.size(); i++) {
            if (i != events.size() - 1) {
                stringEvents += events.get(i) + ",";
            } else {
                stringEvents += events.get(i);
            }

        }
        return stringEvents;
    }

    public void fillUserEvents() {
        for (int j = 0; j < usersId.size(); j++) {
            String idUser = usersId.get(j);
            //System.out.println(idUser);
            List<Integer> groups = new ArrayList<Integer>(getGroupsByUserId(idUser));
            /*for (int i = 0; i < groups.size(); i++) {
                System.out.println("List size:" + groups.size() + "Element:" + groups.get(i));
            }*/

            List<String> events = new ArrayList<String>(getEventsByListGroups(groups));

            /*for (int i = 0; i < groups.size(); i++) {
                System.out.println("List size2:" + events.size() + "Event:" + events);
            }*/
            String eventClean = events.toString();
            eventClean = eventClean.substring(1, eventClean.length() - 1);
            usersEvents.add(idUser + "\t" + (eventClean));

        }

    }

    public void writeSampleUsersEvents() {
        File file = new File(pathSampleUsersEvents);
        FileWriter fr = null;
        try {
            fr = new FileWriter(file, true);
            for (int j = 0; j < usersEvents.size(); j++) {
                fr.write(usersEvents.get(j));
                fr.write("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // close resources
            try {
                fr.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}