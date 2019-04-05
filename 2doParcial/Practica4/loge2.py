#---------------------------------------------------------
# Práctica #4: Concurrent Futures en Python
# Fecha: 24-Feb-2019
# Autores:
#          A01370880 Rubén Escalante Chan
#          A01377162 Guillermo Pérez Trueba
#---------------------------------------------------------

from time import time
from concurrent.futures import ProcessPoolExecutor
from functools import reduce
from operator import add

N = 5_000_000
NUM_PROCS = 8

def measure_time(fun, *args):
  start = time()
  result = fun(*args)
  end = time()
  return (result, end - start)

def loge2_sequential(n):
  r = 0
  for i in range(1, n + 1):
    r += ((-1) ** (i + 1)) / i
  return r

def loge2_range(range_tuple):
  start, end = range_tuple
  r = 0
  for i in range(start, end + 1):
    r += ((-1) ** (i + 1)) / i
  return r

def make_ranges(total, chunks):
  assert total % chunks == 0, f'{total} is not exactly divisible by {chunks}.'
  size = total // chunks
  return [(i * size + 1, (i + 1) * size) for i in range(chunks)]

def loge2_parallel():
  with ProcessPoolExecutor() as pool:
    results = list(pool.map(loge2_range, make_ranges(N, NUM_PROCS)))
  return reduce(add, results)

def main():
  rs, ts = measure_time(loge2_sequential, N)
  print(f'T1={ts:.4f}, Result={rs}')

  rp, tp = measure_time(loge2_parallel)
  print(f'T{NUM_PROCS}={tp:.4f}, Result={rs}')

  print(f'S{NUM_PROCS}={ts/tp:.4f}')

main()