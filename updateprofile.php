<?php

include 'conn.php';

$id= $_POST['id'] ?? 0;
$name = $_POST['name'] ?? '';
$email = $_POST['email'] ?? '';
$country = $_POST['country'] ?? '';
$city = $_POST['city'] ?? '';
$phone = $_POST['phone'] ?? '';
$profpic = $_POST['profilepic'] ?? '';

$query = "UPDATE users SET name='$name', country='$country', email='$email', city='$city', phone='$phone', profilepic='$profpic' WHERE UserID=$id";

if (mysqli_query($conn, $query)) {
    echo "Record updated successfully";
} else {
    echo "Error updating record: " . mysqli_error($conn);
}

mysqli_close($conn);

?>