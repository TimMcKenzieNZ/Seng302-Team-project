package seng302.model.enums;

/**
 * Enum that stores the server and endpoint addresses.
 */
public enum ADDRESSES {

    // Servers
    SERVER("server", "http://localhost:80/api/v1/"), // set to local server by default
    LOCAL_SERVER("local server", "http://localhost:80/api/v1/"),
    VM_SERVER("online server", "http://csse-s302g6.canterbury.ac.nz/api/v1/"),

    // Authentication endpoints
    LOGIN("POST login", "login"),

    // Donor endpoints
    POST_DONOR("POST one donor", "donors/"),
    POST_PHOTO("POST one photo", "donors/"),
    GET_DONOR("GET one donor", "donors/"),
    GET_DONORS("GET all donors", "donors"),
    DELETE_PHOTO("DELETE one photo", "donors/"),
    DONOR_PHOTO("GET or POST donors photo", "donors/%s/photo"),
    USER_ATTRIBUTE_COLLECTION("PATCH donors user attribute collection", "donors/%s/userAttributeCollection"),
    DONOR_ORGANS("PATCH donor organs", "donors/%s/donorOrgans"),
    PATCH_RECEIVER_ORGANS("PATCH receiver records of a donor", "donors/%s/receiverOrgans"),
    USER_PROFILE("PATCH donor profile", "donors/%s/profile"),
    USER_BASIC_INFO("PATCH donor basic info", "donors/%s/basicInformation"),
    USER_CONTACT_DETAILS("PATCH donor contact details", "donors/%s/contactDetails"),
    USER_ILLNESSES("PATCH for donor illnesses", "donors/%s/illnesses"),
    DELETE_ILLNESSES("DELETE an illness", "donors/%s/illnesses"),
    USER_PROCEDURE("PATCH or POST donor procedure", "donors/%s/procedures"),
    POST_DEATH_DETAILS("Posts new death details for one user", "donors/%s/deathDetails"),
    USER_MEDICATION("POST or PATCH a donor medication", "donors/%s/medication"),

    // Clinician endpoints
    GET_CLINICIAN("GET one clinician", "clinicians/"),
    GET_CLINICIANS("GET all clinicians", "clinicians"),

    PATCH_CLINICIAN("PATCH one clinician", "clinicians/"),
    POST_CLINICIAN("POST one clinician", "clinicians/"),
    POST_CLINICIAN_OVERWRITE("POST one clinician and overwrites if its a duplicate", "import/singleImport/clinician/"),
    CLINICIAN_PHOTO("GET or POST clinician photo", "clinicians/%s/photo"),
    DELETE_CLINICIAN("DELETE one clinician", "clinicians/"),

    // Admin endpoints
    GET_ADMIN("GET one admin", "admins/"),
    GET_ADMINS("GET all admin", "admins"),
    POST_ADMIN("POST one admin", "admins"),
    POST_ADMIN_OVERWRITE("POST one admin and overwrites if its a duplicate", "import/singleImport/admin/"),
    PATCH_ADMIN("PATCH one or more attributes of an administrator", "admins/"),

    DELETE_ADMIN("Delete one admin", "admins/"),

    //Transplant waiting list endpoint
    GET_TRANSPLANT_WAITING_LIST("GET all records", "donors/transplantWaitingList"),

    // Import single donor json
    POST_IMPORT_DONOR_SINGLE("POST one donor JSON", "import/singleImport/donor"),
    POST_IMPORT_CSV("POST one csv", "import/csv"),

    // Statistics/data endpoints
    GET_RECEIVER_ORGANS("Get counts of receiver organs", "getReceiverDataByOrgans"),
    GET_DONOR_ORGANS("Get counts of donors organs", "getDonorDataByOrgans"),
    GET_ALL_RECEIVED_ORGANS(" get counts of all types of organ received per region", "getOrganDataByRegion"),
    GET_ALL_DONATED_ORGANS(" get counts of all types of organ donations per region", "getDonorOrganDataByRegion"),
    GET_DONOR_ORGANS_REGION("Get the count of organs as marked as donating per region", "getDonorRegion"),
    GET_RECEIVER_ORGANS_DATE_RANGE("Get the counts of organs marked as receiving within a time range", "getTransplantListOrgans"),
    GET_RECEIVER_ORGANS_YEAR("Gets the count of organs marked as receiving as a map for a given year", "getTransplantListOrgansYear"),
    GET_RECEIVER_ORGANS_REGION("Gets the count of organs as marked as receiving as a map for a given region", "getReceiverRegion"),
    GET_TOTAL_RECEIVER_ORGANS_RANGE("Gets the count of organs marked as receiving as an int for a time range","getTransplantListOrgansTotal"),
    GET_TOTAL_RECEIVER_ORGANS_YEAR("Gets the count of organs marked as receiving as an int for a year", "getTransplantListOrgansTotalYear"),
    //Country endpoints
    GET_COUNTRIES("Get all of the countries", "countries"),
    DELETE_COUNTRY("Deletes a single country", "countries/%s"),
    POST_COUNTRY("Adds a new country to the list", "countries/%s");

    private String name;
    private String address;

    ADDRESSES(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public static void setLocalServer() {
        SERVER.address = LOCAL_SERVER.address;
    }

    public static void setVmServer() {
        SERVER.address = VM_SERVER.address;
    }
}
