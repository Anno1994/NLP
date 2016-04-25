package jmh;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

import bank1.Bank;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

/**
 * Created by anatolij on 25.04.16.
 */

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class BankBenchmarks {

    @Threads(8)
    @State(Scope.Benchmark)
    public static class StateClass {
        public Bank bank = new Bank();
        public long acc1 = bank.createAccount();
        public long acc2 = bank.createAccount();
    }

    @Benchmark
    @Group("deposit_only")
    @GroupThreads(4)
    public int deposit_only1(StateClass state){
        state.bank.deposit(state.acc1, 1);
        return state.bank.getAccountBalance(state.acc1);
    }

    @Benchmark
    @Group("deposit_only")
    @GroupThreads(4)
    public int deposit_only2(StateClass state) {
        state.bank.deposit(state.acc2, 1);
        return state.bank.getAccountBalance(state.acc2);
    }

    @Benchmark
    @Group("withdraw_only1")
    @GroupThreads(4)
    public int withdraw_only1(StateClass state) {
        state.bank.withdraw(state.acc1, 1);
        return state.bank.getAccountBalance(state.acc1);
    }

    @Benchmark
    @Group("withdraw_only2")
    @GroupThreads(4)
    public int withdraw_only2(StateClass state){
        state.bank.withdraw(state.acc2, 1);
        return state.bank.getAccountBalance(state.acc2);
    }

    @Benchmark
    @Group("withdraw_deposit_check")
    @GroupThreads(3)
    public int withdraw(StateClass state) {
        state.bank.withdraw(state.acc1, 1);
        return state.bank.getAccountBalance(state.acc1);
    }

    @Benchmark
    @Group("withdraw_deposit_check")
    @GroupThreads(3)
    public int deposit(StateClass state) {
        state.bank.withdraw(state.acc1, 1);
        return state.bank.getAccountBalance(state.acc1);
    }

    @Benchmark
    @Group("withdraw_deposit_check")
    @GroupThreads(3)
    public int check(StateClass state) {
        return state.bank.getAccountBalance(state.acc1);
    }


    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder().include(".*.BankBenchmarks.*").warmupIterations(5)
                .measurementIterations(5).measurementTime(TimeValue.milliseconds(2000)).forks(2)
                // .result("results.csv")
                // .resultFormat(ResultFormatType.CSV)
                .build();
        new Runner(options).run();
    }
}
