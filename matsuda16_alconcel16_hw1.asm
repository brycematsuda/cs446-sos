#This program counts to 9000 in threes

SET R0 0 #number to increment by 3 then print
SET R1 9000 #end number
SET R2 3 #add three every time
SET R4 1 #console ID

:loop
ADD R0 R2 R0
BNE R0 R1 loop

