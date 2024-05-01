<?php

include 'conn.php';

$userID1=$_POST['userID1'] ?? '';
$userID2=$_POST['userID2'] ?? '';

$query="
SELECT * FROM chats 
WHERE (userID1 = '$userID1' AND userID2 = '$userID2') 
OR (userID1 = '$userID2' AND userID2 = '$userID1')
";
$result = mysqli_query($conn, $query);

if (mysqli_num_rows($result) > 0) {

    $row = mysqli_fetch_assoc($result);
    echo json_encode($row);
} 
else {

    $insertQuery = "INSERT INTO chats (userID1, userID2) VALUES ('$userID1', '$userID2')";
    if (mysqli_query($conn, $insertQuery)) 
    {
        $chatID = mysqli_insert_id($conn); // Get the ID of the newly inserted record
        $newChatQuery = "SELECT * FROM chats WHERE chatID = $chatID";
        $newChatResult = mysqli_query($conn, $newChatQuery);
        
        if ($newChatResult && mysqli_num_rows($newChatResult) > 0)   
        {
            $newRow = mysqli_fetch_assoc($newChatResult);
            echo json_encode($newRow); // Return the newly created chat information
        } 
        else 
        {
            echo "Error retrieving the new chat";
        }

    } 
    else 
    {
        echo "Error creating new chat: " . mysqli_error($conn);
    }

}

 mysqli_close($conn);

 ?>