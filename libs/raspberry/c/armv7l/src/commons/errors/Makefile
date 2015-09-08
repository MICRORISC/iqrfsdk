liberrors.a: errors.o
	ar cru liberrors.a errors.o

errors.o: errors.c
	gcc -march=armv7-a -fPIC -Wall -I ../../../include -c errors.c -o errors.o

tobin:
	mv liberrors.a ../../../bin

clean:
	rm -rf *.o liberrors.a
