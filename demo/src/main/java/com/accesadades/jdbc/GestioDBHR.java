package com.accesadades.jdbc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class GestioDBHR {
//Com veurem, aquesta booleana controla si volem sortir de l'aplicació.
    static boolean sortirapp = false;
    static boolean DispOptions = true;
        
        public static void main(String[] args) {
    
            try (BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in))) {
    
                try {
                    // Carregar propietats des de l'arxiu
                    Properties properties = new Properties();
                    try (InputStream input = GestioDBHR.class.getClassLoader().getResourceAsStream("config.properties")) {
                    //try (FileInputStream input = new FileInputStream(configFilePath)) {
                        properties.load(input);
        
                        // Obtenir les credencials com a part del fitxer de propietats
                        String dbUrl = properties.getProperty("db.url");
                        String dbUser = properties.getProperty("db.username");
                        String dbPassword = properties.getProperty("db.password");
        
                        // Conectar amb MariaDB
                        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
                            System.out.println("Conexió exitosa");
        
                            String File_create_script = "db_scripts/DB_Schema_HR.sql" ;
        
                            InputStream input_sch = GestioDBHR.class.getClassLoader().getResourceAsStream(File_create_script);
        
                            CRUDEmployees crudbhr = new CRUDEmployees();
                            //Aquí farem la creació de la database i de les taules, a més d'inserir dades
                            crudbhr.CreateDatabase(connection,input_sch);
                            while (sortirapp == false) {
                                MenuOptions(br1,crudbhr,connection);
                            }

                    } catch (Exception e) {
                        System.err.println("Error al conectar: " + e.getMessage());
                    }
                } catch (Exception e) {
                    System.err.println("Error al carregar fitxer de propietats: " + e.getMessage());
                }
            } finally {}
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void MenuOptions(BufferedReader br, CRUDEmployees crudbhr, Connection connection) 
    throws NumberFormatException, IOException, SQLException, InterruptedException {

        Terminal terminal = TerminalBuilder.builder().system(true).build();

        String message = "";
        message = "==================";
        printScreen(terminal, message);

        message = "CONSULTA BD HR";
        printScreen(terminal, message);

        message = "==================";
        printScreen(terminal, message);


        message = "OPCIONS";
        printScreen(terminal, message);

        message = "1. CARREGAR TAULA";
        printScreen(terminal, message);

        message = "2. CONSULTAR TOTES LES DADES";
        printScreen(terminal, message);

        message = "3. ALTRES CONSULTES";
        printScreen(terminal, message);

        message = "4. INSERIR NOU REGISTRE";
        printScreen(terminal, message);

        message = "5. MODIFICAR REGISTRE";
        printScreen(terminal, message);

        message = "6. ESBORRAR REGISTRE";
        printScreen(terminal, message);

        message = "9. SORTIR";
        printScreen(terminal, message);


        message = "Introdueix l'opcio tot seguit >> ";
        for (char c : message.toCharArray()) {
            terminal.writer().print(c);
            terminal.flush();
            Thread.sleep(10);
        }

        int opcio = Integer.parseInt(br.readLine());

        switch(opcio) {
            case 1:
                String File_data_script = "db_scripts/DB_Data_HR.sql" ;
    
                InputStream input_data = GestioDBHR.class.getClassLoader().getResourceAsStream(File_data_script);

                if (crudbhr.CreateDatabase(connection,input_data) == true) {
                    System.out.println("Registres duplicats");
                } else {
                    System.out.println("Registres inserits amb éxit");
                }

                break;
            case 2:
                //Preguntem de quina taula volem mostrar
                MenuSelect(br,crudbhr,connection);
                break;

            case 3:
                MenuSelectAltres(br,crudbhr,connection);
                break;

            case 4:
                MenuInsert(br,crudbhr,connection);
                break;

            case 5:
                MenuUpdateDelete(br,crudbhr,connection,5);
                break;

            case 6:
                MenuUpdateDelete(br,crudbhr,connection,6);
                break;

            case 9:
                //sortim
                System.out.println("Adéu!!");
                sortirapp = true;
                break;
            default:
                System.out.print("Opcio no vàlida");
                MenuOptions(br,crudbhr,connection);
        }
    
    }

    private static void printScreen(Terminal terminal, String message) throws InterruptedException {
        for (char c : message.toCharArray()) {
            terminal.writer().print(c);
            terminal.flush();
            Thread.sleep(10);
        }
        System.out.println();
    }

    public static void MenuSelect(BufferedReader br, CRUDEmployees crudbhr,Connection connection) 
    throws SQLException, NumberFormatException, IOException {

        int opcio = 0;

        while (DispOptions) {

            System.out.println("De quina taula vols mostrar els seus registres?");
            System.out.println("1. Departaments");
            System.out.println("2. Tasques");
            System.out.println("3. Històric de tasques");
            System.out.println("4. Empleats");
            System.out.println("5. Sortir");

            System.out.print("Introdueix l'opció tot seguit >> ");

            opcio = Integer.parseInt(br.readLine());

            switch(opcio) {
                case 1:
                    crudbhr.ReadAllDatabase(connection, "DEPARTMENTS");
                    break;
                case 2:
                    crudbhr.ReadAllDatabase(connection, "JOBS");
                    break;
                case 3:
                    crudbhr.ReadAllDatabase(connection, "JOB_HISTORY");
                    break;
                case 4:
                    crudbhr.ReadAllDatabase(connection, "EMPLOYEES");
                    break;
                case 5:

                    DispOptions = false;
                    break;
                default:
                    System.out.print("Opcio no vàlida");
            }
                
            if (DispOptions) {
                System.out.println("Vols fer altra consulta? (S o N): ");
                String opcioB = br.readLine();
        
                if (opcioB.equalsIgnoreCase("n")){
                    System.out.println("No, no marxis si us plau!");
                    DispOptions = false;
                    break;
                } 
            }
        }
    }

    public static void MenuSelectAltres(BufferedReader br, CRUDEmployees crudbhr,Connection connection) 
    throws SQLException, NumberFormatException, IOException {

        int opcio = 0;

        while (DispOptions) {

            System.out.println("Quina consulta vols fer?");
            System.out.println("1. Departament per id");
            System.out.println("2. Rang de salaris d'empleats");

            System.out.print("Introdueix l'opció tot seguit >> ");

            opcio = Integer.parseInt(br.readLine());

            switch(opcio) {
                case 1:
                    System.out.println("Introdueix la id del departament >> ");
                    int idDept = Integer.parseInt(br.readLine());
                    crudbhr.ReadDepartamentsId(connection, "DEPARTMENTS", idDept);
                    break;
                case 2:
                    System.out.println("Introdueix el salari mínim dins el rang >> ");
                    float salMin = Float.parseFloat(br.readLine());
                    System.out.println("Introdueix el salari màxim dins el rang >> ");
                    float salMax = Float.parseFloat(br.readLine());
                    crudbhr.ReadSalaries(connection, "EMPLOYEES", salMin,salMax);
            }

        }

    }

    public static void MenuInsert(BufferedReader br, CRUDEmployees crudbhr,Connection connection) 
    throws SQLException, NumberFormatException, IOException {

        boolean insertMore = true;

        while (insertMore) {

            boolean dadaValida = true;

            System.out.println("Introdueix els detalls del nou empleat");

            int idEmpl = 0;

            while (dadaValida) {
                System.out.print("Quina és la id (PK) de l'empleat? >> ");

                try {

                    idEmpl = Integer.parseInt(br.readLine());

                    if (idEmpl <= 0) {
                        System.out.println("Format numèric no vàlid");

                    } else {
                        dadaValida = false;
                    }
                    
                } catch (Exception e) {
                    System.out.println("Format numèric no vàlid");
                }

            }

            System.out.print("Introdueix el nom de l'empleat >> ");
            String nomEmp = br.readLine();

            System.out.print("Introdueix el cognom de l'empleat >> ");
            String cognomEmp = br.readLine();

            System.out.print("Introdueix l'email de l'empleat >> ");
            String emailEmp = br.readLine();

            System.out.print("Introdueix el telèfon de l'empleat >> ");
            String telEmp = br.readLine();

            dadaValida = true;

            String hireDateEmp = "";

            while (dadaValida) {
                System.out.print("Introdueix la data d'incorporació de l'empleat (AAAA-MM-DD) >> ");

                try {

                    hireDateEmp = br.readLine();

                    if (!hireDateEmp.matches("^(\\d{4})-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$")) {
                        System.out.println("Format de data no vàlid");

                    } else {
                        dadaValida = false;
                    }
                    
                } catch (Exception e) {
                    System.out.println("Format de data no vàlid");
                }
            }

            System.out.print("Introdueix la id de tasca l'empleat >> ");
            String jobIdEmp = br.readLine();
            
            float salariEmpl = 0;
            dadaValida = true;

            while (dadaValida) {
                System.out.print("Introdueix el salari anual de l'empleat >> ");

                try {

                    salariEmpl = Float.parseFloat(br.readLine());

                    if ( salariEmpl <= 0)   {
                        System.out.println("Salari no pot ser 0 o negatiu");

                    } else {
                        dadaValida = false;
                    }
                    
                } catch (Exception e) {
                    System.out.println("Format de salari no vàlid");
                }

            }
            
            float comPCTEmpl = 0;
            dadaValida = true;

            while (dadaValida) {
                System.out.print("Introdueix les comissions a percebre de l'empleat >> ");

                try {

                    comPCTEmpl = Float.parseFloat(br.readLine());

                    if ( comPCTEmpl <= 0)   {
                        System.out.println("Comissió no pot ser 0 o negativa");

                    } else {
                        dadaValida = false;
                    }
                    
                } catch (Exception e) {
                    System.out.println("Format de comissió no vàlida");
                }
            }

            dadaValida = true;

            int idManagerEmpl = 0;

            while (dadaValida) {
                System.out.print("Quina és la id del manager de l'empleat? >> ");

                try {

                    idManagerEmpl = Integer.parseInt(br.readLine());

                    if ( idManagerEmpl <= 0)   {
                        System.out.println("id Manager no pot ser 0");

                    } else {
                        dadaValida = false;
                    }
                    
                } catch (Exception e) {
                    System.out.println("Format de Id de Manager no vàlid");
                }

            }

            int idDeptEmpl = 0;
            dadaValida = true;

            while (dadaValida) {
                System.out.print("Quina és la id del departament de l'empleat? >> ");

                try {

                    idDeptEmpl = Integer.parseInt(br.readLine());

                    if ( idDeptEmpl <= 0)   {
                        System.out.println("id departament no pot ser 0");

                    } else {
                        dadaValida = false;
                    }
                    
                } catch (Exception e) {
                    System.out.println("Format de Id de Departament no vàlid");
                }
            }

            String bonusEmp = "";
            dadaValida = true;

            while (dadaValida) {
                System.out.print("Quina és el bonus de l'empleat? >> ");

                bonusEmp = br.readLine();

                if ( !bonusEmp.matches("^[1-9][0-9]*$"))   {
                    System.out.println("bonus ha de ser numèric i positiu");

                } else {
                    dadaValida = false;
                }

            }

            Employees emp = new Employees(idEmpl, nomEmp, cognomEmp, emailEmp, telEmp, hireDateEmp, jobIdEmp, 
            salariEmpl, comPCTEmpl, idManagerEmpl, idDeptEmpl, bonusEmp);

            crudbhr.InsertEmployee(connection, emp);

            System.out.println("Vols afegir un altre empleat?");

            if (!br.readLine().matches("[sS]")) {
                insertMore = false;
            }

        }
                            
        }

        public static void MenuUpdateDelete(BufferedReader br, CRUDEmployees crudbhr,Connection connection, int updDel) 
        throws SQLException, NumberFormatException, IOException {

            Employees emp = new Employees();

            System.out.println("Digues la id de l'empleat: ");

            emp.setEmployeeId(Integer.parseInt(br.readLine()));

            if (updDel == 5) {

                boolean updateValOpt = true;
                boolean updateMore = true;

                while (updateMore == true) {

                    while (updateValOpt) {

                        System.out.println("Quina de les següents modificacions vols fer?: ");
                        System.out.println("1. Telèfon i email");
                        System.out.println("2. Salari i comissions");

                        int opcUpdate = Integer.parseInt(br.readLine());

                        if (opcUpdate == 1) {
                            System.out.println("Quin és el nou email?: ");
                            emp.setEmail(br.readLine());
                            System.out.println("Quin és el nou telèfon?: ");
                            emp.setPhoneInt(br.readLine());
                            crudbhr.UpdateEmployee(connection,opcUpdate,emp);
                            updateValOpt = false;
                        } else if (opcUpdate == 2) {
                            System.out.println("Quin és el nou salari?: ");
                            emp.setSalary(Float.parseFloat(br.readLine()));
                            System.out.println("Quines són les noves comissions?: ");
                            emp.setCommissionPct(Float.parseFloat(br.readLine()));
                            crudbhr.UpdateEmployee(connection,opcUpdate,emp);
                            updateValOpt = false;
                        } else {
                            System.out.println("Opció no vàlida");
                        }
                    } 

                    System.out.println(updateMore);

                    System.out.println("Vols fer cap més modificació del mateix empleat? (S o N): ");

                    if (br.readLine().equalsIgnoreCase("N")) {
                        updateMore = false;
                    } else {
                        updateValOpt = true;
                    }

                }
                    
            } else if (updDel == 6) {

                crudbhr.DeleteEmployee(connection, emp);
            }
                
        } 

}
