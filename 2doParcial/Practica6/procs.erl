%----------------------------------------------------------
% Práctica #6: Erlang concurrente
% Date: March 31, 2019.
% Authors:
%          A01370880 Rubén Escalante Chan
%          A01377162 Guillermo Pérez Trueba
%----------------------------------------------------------
-module(procs).
-export([factorial/1, fibo_proc/0, fibo_send/2, double/1, ring/2, star/2]).

% 1
factorial(N) ->
  P = spawn(fun fact_aux/0),
  P ! {fact, N, self()},
  receive
    Result -> Result
  end.

fact_aux() ->
  receive
    {fact, N, Remitente} -> Remitente ! fact(N)
  end.

fact(0) -> 1;
fact(N) when N > 0 -> N * fact(N - 1).

% 2
fibo_proc() -> spawn(fun () -> fibo_loop([1, 0]) end).

fibo_send(Proc, Mssg) ->
  case is_process_alive(Proc) of
    true ->
      Proc !  {Mssg, self()},
      receive
        X -> X
      end;
    false ->
      killed
  end.

fibo_loop(Nums) ->
  [X, Y | T] = Nums,
  receive
    {recent, Remitente} ->
      Remitente ! X,
      fibo_loop(Nums);
    {span, Remitente} ->
      Remitente ! length(Nums),
      fibo_loop(Nums);
    {_, Remitente} ->
      Remitente ! killed
  after 1000 ->
    fibo_loop([X + Y, X, Y | T])
  end.

% 3
double(M) -> 
  A = spawn(fun () -> double_aux(M) end),
  io:format("Created ~w~n", [A]),
  B = spawn(fun () -> double_aux(M) end),
  io:format("Created ~w~n", [B]),
  A ! {first, M - 1, B}.

double_aux(N) ->
  receive
    {_, M, Otro} when M > 0 ->
      io:format("~w recieved message ~w/~w~n", [self(), N - M, N]),
      Otro ! {M, self()},
      double_aux(N);
    {M, Otro} when M > 0 ->
      io:format("~w recieved message ~w/~w~n", [self(), N - M, N]),
      Otro ! {first, M - 1, self()},
      double_aux(N);
    {_, 0, Otro} ->
      io:format("~w recieved message ~w/~w~n", [self(), N, N]),
      Otro ! {0},
      io:format("~w finished~n", [self()]);
    {0} ->
      io:format("~w recieved message ~w/~w~n", [self(), N, N]),
      io:format("~w finished~n", [self()])
  end.

% 4
ring(N, M) ->
  Manager = self(),
  io:format("Current process is ~w~n",[Manager]),
  spawn(fun() -> start(N,M,Manager) end),
  ok.

start(N,M,Manager) ->
  Pid = spawn(fun() -> start(N-1,M,self(),Manager) end),
  io:format("Created ~w~n",[Pid]),
  ring_aux(1, M, Pid, Manager).

start(0, M, Proc,Manager) ->
  Proc ! start,
  ring_aux(1, M, Proc,Manager);

start(N, M, Proc,Manager) ->
  Pid = spawn(fun() -> start(N-1, M, Proc,Manager) end),
  io:format("Created ~w~n",[Pid]),
  ring_aux(1, M, Pid,Manager).

ring_aux(Count, M, _, _) when Count > M -> 
    io:format("~w: finished~n",[self()]);

ring_aux(Count, M, Remitente,Manager) ->
    receive
        start ->
            io:format("~w: Received ~w/~w from ~w~n", [self(), Count, M, Manager]),
            Remitente ! {start, self()},
            ring_aux(Count+1, M, Remitente, Manager);
        {start, Pid} ->
            io:format("~w: Received ~w/~w from ~w~n", [self(), Count, M, Pid]),
            Remitente ! {start, self()},
            ring_aux(Count+1, M, Remitente, Manager)
    end.
  
% 5
star(N, M) ->
  io:format("Current process is ~w~n", [self()]),
  Center = spawn(fun () -> star_center(M) end),
  io:format("Created ~w (center)~n", [Center]),
  Center ! {self(), star_spawn([], N - 1, M)}.

star_spawn(A, N, M) when N > 0 ->
  P = spawn(fun () -> star_son(M) end),
  io:format("Created ~w~n", [P]),
  star_spawn(A ++ [P], N - 1, M);
star_spawn(A, 0, M) ->
  P = spawn(fun () -> star_son(M) end),
  io:format("Created ~w~n", [P]),
  A ++ [P].

star_center(M) ->
  receive
    {Origin, Children} ->
      io:format("~w received ~w/~w from ~w~n", [self(), 0, M, Origin]),
      send_children(M, M - 1, Children),
      io:format("~w finished~n", [self()])
  end.

send_children(M, X, Children) when X > 0 ->
  send_each(M, X, Children),
  send_children(M, X - 1, Children);
send_children(M, X, Children) ->
  send_each(M, X, Children).

send_each(M, X, [H | T]) ->
  receive
    {Origin, I} ->
      io:format("~w received ~w/~w from ~w~n", [self(), M - I, M, Origin]),
      H ! {self(), I}
  after 1000 ->
    H ! {self(), X}
  end,
  send_each(M, X, T);
send_each(M, _, []) ->
  receive
    {Origin, I} ->
      io:format("~w received ~w/~w from ~w~n", [self(), M - I, M, Origin])
  end.

star_son(M) ->
  receive
    {Origin, I} when I =< 0 ->
      io:format("~w received ~w/~w from ~w~n", [self(), M - I, M, Origin]),
      Origin ! {self(), I} ,
      io:format("~w finished~n", [self()]);
    {Origin, I} ->
      io:format("~w received ~w/~w from ~w~n", [self(), M - I, M, Origin]),
      Origin ! {self(), I},
      star_son(M)
  end.