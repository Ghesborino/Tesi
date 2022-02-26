import os
import sys
import time
import threading
import multiprocessing


def attempt_t(tempo_zero: float, tempo_esecuzione: float, lista: list[int], indice: int) -> None:

    lista[indice] = 0

    tempo = time.perf_counter()
    while (tempo - tempo_zero) < tempo_esecuzione:
        lista[indice] += 1
        tempo = time.perf_counter()


def attempt_p(tempo_zero: float, tempo_esecuzione: float, operazioni :multiprocessing.Value) -> int:

    tempo = time.perf_counter()
    while (tempo - tempo_zero) < tempo_esecuzione:
        operazioni.value += 1
        tempo = time.perf_counter()

    return operazioni


def mt(tempo_esecuzione: float, esecutori: int) -> int:

    tempo_zero = time.perf_counter()

    threads = []
    risultati = [0] * esecutori
    for i in range(esecutori):
        threads.append(threading.Thread(target=attempt_t, args=(tempo_zero, tempo_esecuzione, risultati, i)))

    for t in threads:
        t.start()

    for t in threads:
        t.join()

    totale = 0
    for op in risultati:
        totale += op

    return totale


def mp(tempo_esecuzione: float, esecutori: int) -> int:

    tempo_zero = time.perf_counter()

    risultati = []
    for i in range(esecutori):
        risultati.append(multiprocessing.Value("i", 0, lock=False))

    processi = []
    for r in risultati:
        processi.append(multiprocessing.Process(target=attempt_p, args=[tempo_zero, tempo_esecuzione, r]))

    for p in processi:
        p.start()

    for p in processi:
        p.join()

    totale = 0
    for op in risultati:
        totale += op.value

    return totale

if __name__ == '__main__':

    tempo_esecuzione : float = 5.0 if sys.argv.__len__() < 3 else float(sys.argv[1])  # tempo di esecuzione
    esecutori : int = os.cpu_count() if sys.argv.__len__() < 3 else int(sys.argv[2])  # numero di thread/processi

    operazioni_thread : int = mt(tempo_esecuzione, esecutori)  # ottengo il numero di operazioni effettuate con il multithreading
    operazioni_processi : int = mp(tempo_esecuzione, esecutori)  # ottengo il numero di operazioni effettuate con il multiprocessing

    print(f"{'{'}\"esecutori\":{esecutori}, \"tempo_esecuzione\":{tempo_esecuzione}, \"multithreading\":{operazioni_thread}, \"multiprocessing\":{operazioni_processi}{'}'}")