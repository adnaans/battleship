# battleship

A Java project for AP Computer Science in which we had to create a computer AI for Battleship. I created the most advanced AI I could think of at the time, and has a process to take down its opponent.

The first part of the AI is simply firing randomly at an empty board until it gets a hit. As it fires more and more, however, it looks at the size of the ships that are alive, and based on the size of the largest ship that it has not struck down, it will fire in spots that could contain that ship. For example, if the largest ship it has not taken down is a battleship (4 blocks in size), it will only fire at spots that are part of a row or column of empty spots that is 4 or more spaces in length.

The second part of the AI is handling hits. Once the computer hits the user's ship, it will hit in a random spot next to it. If it gets another hit, the computer will continue hitting along that row/column until it sinks the ship. If there is a case in which two ships are side by side, and the computer hit the two ships once each in spots adjacent to each other, and the spots next to them are misses (Board would look like this : OXXO), the computer will hit in the other adjacent spots to the hits until it sinks the ships.
