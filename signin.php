<?php

include 'conn.php';

$email=$_POST['email'] ?? '';
$pass=$_POST['password'] ?? '';

$query="SELECT * FROM users WHERE email = '$email' and password = '$pass'";
$result = mysqli_query($conn, $query);

if (mysqli_num_rows($result) > 0) {

    $row = mysqli_fetch_assoc($result);
    echo json_encode($row);
} 
else {

    echo "0 results";

}

 mysqli_close($conn);

 ?>