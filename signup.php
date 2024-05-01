<?php

include 'conn.php';

$name=$_POST['name'] ?? '';
$email=$_POST['email'] ?? '';
$pass=$_POST['password'] ?? '';
$country=$_POST['country'] ?? '';
$city=$_POST['city'] ?? '';
$phone=$_POST['phone'] ?? '';
$profpic=$_POST['profilepic'] ?? '';

$query="Insert into users(name, email, password, country, city, phone, profilepic, otp) 
        Values ('$name','$email','$pass','$country','$city','$phone','$profpic', 123456)";
        
if (mysqli_query($conn, $query)) {

    echo "Record inserted successfully";

} else {

    echo "Error deleting record: " . mysqli_error($conn);

}

 mysqli_close($conn);

 ?>