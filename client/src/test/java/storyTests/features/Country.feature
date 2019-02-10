#Feature: viewing, inserting, editing, deleting, and saving countries
#  Background:
#    Given I am viewing the countries list
#
#  Scenario: Importing countries
#    When I import countries from file".
#    Then there should be "7" default countries in the list.
#
#  Scenario: View the correct number of countries in country table in alphabetical order.
#    Then I should should see; "ATLANTIS", "BABAR'S KINGDOM", "GONDOR", "MORDOR", "NARNIA", "NEW ZEALAND", and "WAKANDA".
#    And The number of countries in the table should be "7".
#
#  Scenario: inserting into the table in order
#    When I enter "GENOVIA" into the insert box.
#    Then "GENOVIA" should be added into the table between "BABAR'S KINGDOM" and "GONDOR".
#
#  Scenario: inserting duplicate failure
#    When I have entered "NEW ZEALAND" into the insert box.
#    Then "NEW ZEALAND" should not be added.
#    And the size of the list should be "7".
#
#  Scenario: editing a country
#    When I have entered "GENOVIA" and added it to the country list.
#    And I edit "GENOVIA" name to "LOOMPA LAND".
#    Then "GENOVIA" should be replaced with "LOOMPA LAND"
#
#  Scenario: editing a country failure
#    When I edit "NEW ZEALAND" and try to change its name to "LOOMPA LAND"
#    Then "NEW ZEALAND" will still be in the list.
#    And "LOOMPA LAND" should not be.
#
#  Scenario: deleting a country
#    When I have entered "GENOVIA" and added it to the country list.
#    And I delete "GENOVIA".
#    Then "GENOVIA" will no longer be in the list of countries.
#
#  Scenario: deleting a country fail
#    When I delete "NEW ZEALAND"
#    Then "NEW ZEALAND" should not be deleted and remain in the list of countries.
#
#  Scenario: importing countries
#    When I import a list of countries containing "GENOVIA", "LOOMPA LAND", and "NEW ZEALAND".
#    Then only "GENOVIA" and "LOOMPA LAND" should be added to the country list.
#    And the number of countries is "9".
