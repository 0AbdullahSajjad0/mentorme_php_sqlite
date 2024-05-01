<?php

include 'conn.php';

$id=$_POST['id'] ?? 0;

$query="SELECT * FROM mentors WHERE mentorID = $id";
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