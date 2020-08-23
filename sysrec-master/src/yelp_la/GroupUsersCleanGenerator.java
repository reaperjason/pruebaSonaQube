package test1;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import java.util.List;
import java.util.Scanner;


class GroupUsersCleanGenerator {

    String groupidUsersPath = "data/yelp_la/groupid_users.dat"; //24103 users (0-24102)
    String groupidUsersCleanPath = "data/yelp_la/groupid_users_clean.dat";

    List<Integer> groupIdList = new ArrayList<Integer>();

    public GroupUsersCleanGenerator() {

    }

    public boolean existeIdGroup(int idGroup, List<Integer> groupIdList) {
        for (int i = 0; i < groupIdList.size(); i++) {
            if (idGroup == groupIdList.get(i))
                return true;
        }
        return false;
    }

    public void writeGroupUserCleanFile() {
        System.out.println("Writing in groupid_users_clean.dat...");

        Scanner doc;
        try {    
            doc = new Scanner(new File(groupidUsersPath));
            while (doc.hasNextLine()) {
                String line = doc.nextLine();
                String[] columns = line.split("\t");
                if (!existeIdGroup(Integer.parseInt(columns[0]), groupIdList)) {
                    groupIdList.add(Integer.parseInt(columns[0]));
                    writeFile(line, groupidUsersCleanPath);
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        System.out.println("File groupid_users_clean.dat completed");
    }

    public void writeFile(String line, String path) {
        File file = new File(path);
        FileWriter fr = null;
        try {
            fr = new FileWriter(file, true);
            fr.write(line);
            fr.write("\n");
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