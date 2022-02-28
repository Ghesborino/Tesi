import os
import sys
import ctypes
import threading
import multiprocessing

# funzione per la cifratura
def crypt(testo : str, risultato : multiprocessing.Value) -> None :
    testo_cifrato : str = None
    """ algoritmo di codifica """
    risultato.value = testo_cifrato


# funzione per la decifratura
def decrypt(testo : str, risultato : multiprocessing.Value) -> None:
    testo_decifrato: str = None
    """ algoritmo di decodifica """
    risultato.value = testo_decifrato


# Funzione per l'io e lo spawn dei processi
# operazione: 0 = cifra, 1 = decifra
def io(files : list[str], operazione : int) -> None :

    for file in files :

        with open(file, 'r') as input:

            testo : str = input.read()
            lunghezza : int = testo.__len__()

            manager = multiprocessing.Manager()
            testoP1 = manager.Value(ctypes.c_char_p, None)
            testoP2 = manager.Value(ctypes.c_char_p, None)

            if operazione == 0 :

                p1 = multiprocessing.process(target=crypt, args=[testo[0:(lunghezza/2)], testoP1])
                p2 = multiprocessing.Process(target=crypt, args=[testo[(lunghezza / 2), lunghezza], testoP2])

                p1.start()
                p2.start()

                p1.join()
                p2.join()

                with open(f'crypted{file}', 'w') as output :
                    output.write(f"{str(testoP1.value)}{str(testoP2.value)}")

            elif operazione == 1 :

                p1 = multiprocessing.process(target=decrypt, args=[testo[0:(lunghezza / 2)], testoP1])
                p2 = multiprocessing.Process(target=decrypt, args=[testo[(lunghezza / 2), lunghezza], testoP2])

                p1.start()
                p2.start()

                p1.join()
                p2.join()

                with open(f'decrypted{file}', 'w') as output:
                    output.write(f"{str(testoP1.value)}{str(testoP2.value)}")


if __name__ == '__main__' :

    ts = os.cpu_count() / 2
    files = # lettura lista file da elaborare
    operazione = # lettura operazione da effettuare

    if files.__len__() == 0 or operazione == -1 :
        print("ERRORE: Uno o più argomenti sono invalidi")
        exit(1)

    thds = []
    """ 
        Distribuzione uniforme dei file tra i thread
    """

    for t in thds:
        t.start()

    for t in thds:
        t.join()

    print("AVVISO: Operazioni completate")