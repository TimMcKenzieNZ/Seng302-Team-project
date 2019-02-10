package seng302.model;

import java.io.IOException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.json.JSONObject;
import seng302.model.person.DonorReceiver;

import java.io.IOException;


/**
 * DrugInteractionTask is used to make queries to the eHealthMe drug interactions API off the main
 * gui thread. It requires a DrugInteractionsCache which it uses to store the raw JSON responses for
 * previous queries. It will check if the query exists in the cache before calling the api and will
 * only call the api if the query isnt in the cache or is older than 7 days.
 */
public class DrugInteractionTask extends Task<ObservableList<String>> {

  /**
   * First of two drug names to check for interactions.
   */
  private String drug1;


  /**
   * Second of two drug names to check for interactions.
   */
  private String drug2;


  /**
   * Donor/Receiver to customize interaction results for based on age and gender.
   */
  private DonorReceiver donorReceiver;


  /**
   * DrugInteractions cache to check for existing results and add new results to.
   */
  private DrugInteractionsCache cache;


  /**
   * Standardized drugPair of drug1 and drug2.
   */
  private String drugPair;


  /**
   * The time in minutes after which cached results are considered old and must be updated.
   */
  private int MINUTES_TO_REFRESH_CACHE = 10080; // one week


  /**
   * Constructor for a DrugInteraction task.
   *
   * @param drug1 String drug name 1
   * @param drug2 String drug name 2
   * @param donorReceiver DonorReceiver to customize interactions for
   * @param cache DrugInteractionsCache to check for cached results
   */
  public DrugInteractionTask(String drug1, String drug2, DonorReceiver donorReceiver,
      DrugInteractionsCache cache) {
    this.drug1 = drug1;
    this.drug2 = drug2;
    this.donorReceiver = donorReceiver;
    this.cache = cache;
    this.drugPair = standardizeDrugPair(drug1, drug2);
  }


  /**
   * Used to update the cache by either updating CacheElement or adding CacheElement. Should be
   * called using Platform.runLater() to avoid thread issues!
   *
   * @param cache Cache to update
   * @param key CacheElement key
   * @param value CacheElement value
   */
  public void updateCache(DrugInteractionsCache cache, String key, JSONObject value) {
    if (cache.getCacheElement(key) != null) {
      cache.removeCacheElement(key);
      cache.setCacheElement(key, value);
    } else {
      cache.setCacheElement(key, value);
    }
  }


  /**
   * Standardizes the drug name pair so that whitespace is removed and the pair is ordered
   * alphabetically.
   *
   * @param drug1 drug name 1
   * @param drug2 drug name 2
   * @return result drug name pair
   */
  private String standardizeDrugPair(String drug1, String drug2) {
    drug1 = drug1.toLowerCase();
    drug1 = drug1.replaceAll("\\s", "");
    drug2 = drug2.toLowerCase();
    drug2 = drug2.replaceAll("\\s", "");
    String result;
    if ((drug1.compareToIgnoreCase(drug2)) < 0) {
      result = drug1 + drug2;
    } else {
      result = drug2 + drug1;
    }
    return result;
  }


  /**
   * Start the task
   *
   * @return ObservableList strings containing the drug interactions and the time they usually occur
   * customized for the given donor/receiver
   */
  @Override
  protected ObservableList<String> call() {
    // An observable list to represent the results
    final ObservableList<String> results = FXCollections.<String>observableArrayList();
    // Control boolean
    boolean succeeded = false;

    // First check the cache for existing results
    if ((cache.getCacheElement(drugPair) != null) && (cache.getCacheElement(drugPair).calculateAge()
        < MINUTES_TO_REFRESH_CACHE)) {
      try {
        System.out.println("Getting from cache");
        results.addAll(DrugInteractions.getCustomisedDrugInteractions2(donorReceiver,
            cache.getCacheElement(drugPair).retrieveValue()));
        succeeded = true;
      } catch (Exception e) {
        System.err.println(e);
      }
    }

    // If getting the results from the cache fails.
    if (!succeeded) {
      try {
        System.out.println("Getting from API");
        JSONObject drugInteractions = DrugInteractions.getDrugInteractions(drug1, drug2);
        results.addAll(
            DrugInteractions.getCustomisedDrugInteractions2(donorReceiver, drugInteractions));
        Platform.runLater(() -> {
          updateCache(cache, drugPair, drugInteractions);
        });
      } catch (IOException e) {
        System.err.println(e);
      }
    }

    return results;
  }

  @Override
  protected void cancelled() {
    super.cancelled();
    updateMessage("The task was cancelled.");
  }

  @Override
  protected void failed() {
    super.failed();
    updateMessage("The task failed.");
  }

  @Override
  public void succeeded() {
    super.succeeded();
    updateMessage("The task finished successfully.");
  }
}

