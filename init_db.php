<?php
// init_db.php - Run once to import to Aiven

// 1. Your Aiven connection (get from Aiven dashboard)
$host = "campusconnect-db-dincedyden-d923.j.aivencloud.com"; // <-- replace
$port = 22306; // <-- replace
$user = "avnadmin";
$pass = "AVNS_3iQY7HnujORV3vVmEPQ"; // <-- replace
$db   = "defaultdb";

$conn = new mysqli($host, $user, $pass, $db, $port);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

echo "Connected to Aiven...<br>";

// 2. Read your SQL file
$sql_file = "uni_db.sql";
if (!file_exists($sql_file)) {
    die("uni_db.sql not found in GitHub!");
}

$sql = file_get_contents($sql_file);

// 3. Import
if ($conn->multi_query($sql)) {
    echo "Importing...<br>";
    do {
        // clear results
        if ($result = $conn->store_result()) {
            $result->free();
        }
    } while ($conn->next_result());
    
    echo "<h2>✅ DONE! Database imported to Aiven</h2>";
    echo "Now DELETE this file (init_db.php) from GitHub for security.";
} else {
    echo "Error: " . $conn->error;
}

$conn->close();
?>
