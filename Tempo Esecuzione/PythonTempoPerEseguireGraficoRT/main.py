import os
import sys
import time
import math

from concurrent.futures import ThreadPoolExecutor
from concurrent.futures import ProcessPoolExecutor


# funzione che simula il singolo thread
def attempt(numero_richieste: int = 1000) -> str:

    for _ in range(numero_richieste):
        # Simulo il tempo della singola richiesta attraverso il ciclo seguente
        for i in range(1000):
            math.sqrt(i)
    return "Done"


# Metodo del multithreading
def threadings(numero_richieste: int = 10000, numero_thread: int = 10) -> float:

    start = time.perf_counter()

    params = [int(math.ceil(numero_richieste / numero_thread))] * numero_thread
    executor = ThreadPoolExecutor()
    results = executor.map(attempt, params)

    for r in results:
        if (r != "Done"):
            print("Invalid result")
            break

    finish = time.perf_counter()

    return (finish - start)


# Metodo del multiprocessing
def processings(numero_richieste: int = 10000, numero_processi: int = 10) -> float:

    start = time.perf_counter()
    params = [int(math.ceil(numero_richieste / numero_processi))] * numero_processi

    executor = ProcessPoolExecutor()
    results = executor.map(attempt, params)

    for r in results:
        if (r != "Done"):
            print("Invalid result")
            break

    finish = time.perf_counter()

    return (finish - start)


if __name__ == '__main__':

    richieste = 10000 if sys.argv.__len__() < 3 else int(sys.argv[1])  # numero di richieste complessive
    esecutori = os.cpu_count() if sys.argv.__len__() < 3 else int(sys.argv[2])  # numero di thread/processi

    threading_time = threadings(richieste, esecutori)  # ottengo il tempo impiegato dal metodo con multithreading
    processing_time = processings(richieste, esecutori)  # ottengo il tempo impiegato dal metodo con multiprocessing

    print(f"{'{'}\"richieste\":{richieste}, \"esecutori\":{esecutori}, \"multithreading\":{threading_time}, \"multiprocessing\":{processing_time}{'}'}")
