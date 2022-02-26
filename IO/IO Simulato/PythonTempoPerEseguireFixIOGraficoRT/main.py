import os
import sys
import time
import threading
import multiprocessing

def io_bound(sec : float) -> None :
    time.sleep(sec)

def mt(tempo : float, esecutori : int) -> float :

    tempo_zero = time.perf_counter()

    threads = []
    for i in range(esecutori):
        threads.append(threading.Thread(target=io_bound, args=[tempo]))

    for t in threads:
        t.start()

    for t in threads:
        t.join()

    tempo_finale = time.perf_counter()

    return (tempo_finale - tempo_zero) - tempo


def mp(tempo : float, esecutori : int) -> float :

    tempo_zero = time.perf_counter()

    processi = []
    for i in range(esecutori):
        processi.append(multiprocessing.Process(target=io_bound, args=(tempo,)))

    for p in processi:
        p.start()

    for p in processi:
        p.join()

    tempo_finale = time.perf_counter()

    return (tempo_finale - tempo_zero) - tempo


if __name__ == '__main__' :

    tempo = 5.0 if sys.argv.__len__() < 3 else float(sys.argv[1])  # numero di richieste complessive
    esecutori = os.cpu_count() if sys.argv.__len__() < 3 else int(sys.argv[2])  # numero di thread/processi

    threading_time = mt(tempo, esecutori)  # ottengo il tempo impiegato dal metodo con multithreading
    processing_time = mp(tempo, esecutori)  # ottengo il tempo impiegato dal metodo con multiprocessing

    print(f"{'{'}\"tempo\":{tempo}, \"esecutori\":{esecutori}, \"multithreading\":{threading_time}, \"multiprocessing\":{processing_time}{'}'}")