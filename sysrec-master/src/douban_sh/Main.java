class Main {

    public static void main(String[] args) throws Exception {

        // Generate groupid_users_clean.dat
        new GroupUsersCleanGenerator().writeGroupUserCleanFile();
        
        // Generate Samples
        int[][] ngroupsnusers = { { 3, 2 }, { 3, 3 }, { 3, 4 }, { 3, 5 } };
        new SamplesGenerator(ngroupsnusers);

        


    }

} 