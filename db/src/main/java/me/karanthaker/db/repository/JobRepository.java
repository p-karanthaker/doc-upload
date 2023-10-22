package me.karanthaker.db.repository;

import me.karanthaker.db.entity.Job;

public interface JobRepository extends IRepository<Job, Integer> {
    Iterable<Job> findByStatus(Job.Status status);
}
