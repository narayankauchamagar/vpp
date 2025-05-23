package np.com.thapanarayan.vpp.repo;

import np.com.thapanarayan.vpp.entity.Battery;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BatteryRepositoryTest {


    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass")
            .withUrlParam("useSSL", "false")
            .withUrlParam("allowPublicKeyRetrieval", "true");

    @Autowired
    private TestEntityManager entityManager; // Used to persist entities directly for testing

    @Autowired
    private BatteryRepository batteryRepository; // The repository under test

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.MySQLDialect");
        registry.add("spring.jpa.show-sql", () -> "true");
    }

    private Battery batteryA;
    private Battery batteryB;
    private Battery batteryC;
    private Battery batteryD;

    @BeforeEach
    void setUp() {
        // Clear the database before each test to ensure test isolation
        batteryRepository.deleteAll();

        // Initialize and persist sample Battery entities for testing
        batteryA = new Battery();
        batteryA.setName("BatteryA");
        batteryA.setPostcode("12345");
        batteryA.setCapacity(100L);

        batteryB = new Battery();
        batteryB.setName("BatteryB");
        batteryB.setPostcode("12346");
        batteryB.setCapacity(200L);


        batteryC = new Battery();
        batteryC.setName("BatteryC");
        batteryC.setPostcode("12347");
        batteryC.setCapacity(300L);

        batteryD = new Battery();
        batteryD.setName("BatteryD");
        batteryD.setPostcode("12345");
        batteryD.setCapacity(120L);


        // Persist entities using TestEntityManager to ensure they are in the database
        entityManager.persist(batteryA);
        entityManager.persist(batteryB);
        entityManager.persist(batteryC);
        entityManager.persist(batteryD);
        entityManager.flush(); // Flush changes to the database
    }

    @Test
    @Order(1)
    void containerShouldBeRunning() {
        assertTrue(mysql.isRunning());
        assertEquals("testdb", mysql.getDatabaseName());
        System.out.println("MySQL Container URL: " + mysql.getJdbcUrl());
        System.out.println("MySQL Version: " + mysql.getDockerImageName());
    }

    @Test
    @DisplayName("Should find a battery by its name")
    void findBatteryByName_found() {
        // When
        Optional<Battery> foundBattery = batteryRepository.findBatteryByName("BatteryA");

        // Then
        assertThat(foundBattery).isPresent();
        assertThat(foundBattery.get().getName()).isEqualTo("BatteryA");
        assertThat(foundBattery.get().getPostcode()).isEqualTo("12345");
    }

    @Test
    @DisplayName("Should return empty optional when battery name not found")
    void findBatteryByName_notFound() {
        // When
        Optional<Battery> foundBattery = batteryRepository.findBatteryByName("NonExistentBattery");

        // Then
        assertThat(foundBattery).isNotPresent();
    }

    @Test
    @DisplayName("Should find a battery by its postcode")
    void findByPostcode_found() {
        // When
        Optional<Battery> foundBattery = batteryRepository.findByPostcode("12346");

        // Then
        assertThat(foundBattery).isPresent();
        assertThat(foundBattery.get().getName()).isEqualTo("BatteryB");
        assertThat(foundBattery.get().getPostcode()).isEqualTo("12346");
    }

    @Test
    @DisplayName("Should return empty optional when battery postcode not found")
    void findByPostcode_notFound() {
        // When
        Optional<Battery> foundBattery = batteryRepository.findByPostcode("99999");

        // Then
        assertThat(foundBattery).isNotPresent();
    }

    @Test
    @DisplayName("Should find batteries within a given postcode range, ordered by name ascending")
    void findByPostcodeBetween_found() {
        // When
        List<Battery> batteries = batteryRepository.findByPostcodeBetween("12345", "12347");

        // Then
        assertThat(batteries).hasSize(4); // BatteryA, BatteryB, BatteryC, BatteryD
        // Verify order by name ascending
        assertThat(batteries.get(0).getName()).isEqualTo("BatteryA");
        assertThat(batteries.get(1).getName()).isEqualTo("BatteryB");
        assertThat(batteries.get(2).getName()).isEqualTo("BatteryC");
        assertThat(batteries.get(3).getName()).isEqualTo("BatteryD"); // D comes after C alphabetically
    }

    @Test
    @DisplayName("Should return empty list when no batteries found in postcode range")
    void findByPostcodeBetween_notFound() {
        // When
        List<Battery> batteries = batteryRepository.findByPostcodeBetween("90000", "90001");

        // Then
        assertThat(batteries).isEmpty();
    }

    @Test
    @DisplayName("Should find batteries within postcode and capacity range, ordered by name ascending")
    void findByPostcodeBetweenAndCapacityBetween_found() {
        // When
        // Search postcode range 12345-12347, capacity between 100 and 150
        // Expected: BatteryA (100), BatteryC (150), BatteryD (120)
        List<Battery> batteries = batteryRepository.findByPostcodeBetweenAndCapacityBetween("12345", "12347", 100L, 150L);

        // Then
        assertThat(batteries).hasSize(2);
        assertThat(batteries).extracting(Battery::getName)
                .containsExactly("BatteryA", "BatteryD"); // Order by name asc
    }

    @Test
    @DisplayName("Should return empty list when no batteries found in postcode and capacity range")
    void findByPostcodeBetweenAndCapacityBetween_notFound() {
        // When
        List<Battery> batteries = batteryRepository.findByPostcodeBetweenAndCapacityBetween("12340", "12344", 500L, 600L);

        // Then
        assertThat(batteries).isEmpty();
    }

    @Test
    @DisplayName("Should save a new battery")
    void saveBattery() {
        // Given
        Battery newBattery = new Battery();
        newBattery.setName("BatteryE");
        newBattery.setPostcode("54321");
        newBattery.setCapacity(300L);

        // When
        Battery savedBattery = batteryRepository.save(newBattery);

        // Then
        assertThat(savedBattery).isNotNull();
        assertThat(savedBattery.getId()).isNotNull();
        assertThat(savedBattery.getName()).isEqualTo("BatteryE");

        // Verify it can be found by ID
        Optional<Battery> found = batteryRepository.findById(savedBattery.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("BatteryE");
    }

    @Test
    @DisplayName("Should retrieve all batteries")
    void findAllBatteries() {
        // When
        List<Battery> batteries = batteryRepository.findAll();

        // Then
        assertThat(batteries).hasSize(4); // As set up in @BeforeEach
        assertThat(batteries).containsExactlyInAnyOrder(batteryA, batteryB, batteryC, batteryD);
    }

    @Test
    @DisplayName("Should delete a battery by ID")
    void deleteBatteryById() {
        // Given
        Long idToDelete = batteryA.getId();

        // When
        batteryRepository.deleteById(idToDelete);
        entityManager.flush(); // Ensure deletion is committed

        // Then
        Optional<Battery> deletedBattery = batteryRepository.findById(idToDelete);
        assertThat(deletedBattery).isNotPresent();
        assertThat(batteryRepository.findAll()).hasSize(3);
    }
}
