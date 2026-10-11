<?php
$host = "campusconnect-db-dincedyden-d923.j.aivencloud.com"; // your Aiven host
$port = 22306; // your Aiven port
$user = "avnadmin";
$pass = "AVNS_3iQY7HnujORV3vVmEPQ";
$db   = "defaultdb";

$conn = new mysqli($host, $user, $pass, $db, $port);

if ($conn->connect_error) {
    die("❌ NOT connected to Aiven: " . $conn->connect_error);
}

echo "✅ Connected to Aiven!<br><br>";

// Show all tables
$result = $conn->query("SHOW TABLES");
if ($result->num_rows == 0) {
    echo "❌ Connected but NO tables found. Import failed or you uploaded to wrong DB.";
} else {
    echo "✅ Tables found in Aiven:<br>";
    while($row = $result->fetch_array()){
        echo "- " . $row[0] . "<br>";
    }
    
    // Count posts if University Twitter table exists
    $check = $conn->query("SELECT COUNT(*) as total FROM posts");
    if ($check) {
        $count = $check->fetch_assoc();
        echo "<br>Posts table has: " . $count['total'] . " rows";
    }
}
$conn->close();
?>
